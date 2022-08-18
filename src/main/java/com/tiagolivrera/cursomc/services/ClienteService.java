package com.tiagolivrera.cursomc.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tiagolivrera.cursomc.domain.Cidade;
import com.tiagolivrera.cursomc.domain.Cliente;
import com.tiagolivrera.cursomc.domain.Endereco;
import com.tiagolivrera.cursomc.domain.enums.Perfil;
import com.tiagolivrera.cursomc.domain.enums.TipoCliente;
import com.tiagolivrera.cursomc.dto.ClienteDTO;
import com.tiagolivrera.cursomc.dto.ClienteNewDTO;
import com.tiagolivrera.cursomc.repositories.ClienteRepository;
import com.tiagolivrera.cursomc.repositories.EnderecoRepository;
import com.tiagolivrera.cursomc.security.UserSS;
import com.tiagolivrera.cursomc.services.exceptions.AuthorizationException;
import com.tiagolivrera.cursomc.services.exceptions.DataIntegrityException;
import com.tiagolivrera.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

    @Autowired
    private S3Service s3service;

    @Autowired
    private BCryptPasswordEncoder pe;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ImageService imageService;

    @Value("${img.prefix.client.profile}")
    private String prefix;

    public Cliente find(Integer id) {

        UserSS user = UserService.authenticated();
        // o usuario normal logado so pode acessar a si mesmo. apenas o ADMIN pode ler
        // todos os clientes
        if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
            throw new AuthorizationException("Acesso negado");
        }

        Optional<Cliente> obj = clienteRepository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
    }

    @Transactional // salva os clientes e os enderecos na mesma transacao do banco de dados
    public Cliente insert(Cliente obj) {
        obj.setId(null);
        obj = clienteRepository.save(obj);
        enderecoRepository.saveAll(obj.getEnderecos());
        return obj;
    }

    public Cliente update(Cliente obj) {
        Cliente newObj = find(obj.getId());
        updateData(newObj, obj);
        return clienteRepository.save(newObj);
    }

    public void delete(Integer id) {
        find(id);
        try {
            clienteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados");
        }
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
        return clienteRepository.findAll(pageRequest);
    }

    public Cliente fromDTO(ClienteDTO objDTO) {
        return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
    }

    private void updateData(Cliente newObj, Cliente obj) {
        newObj.setNome(obj.getNome());
        newObj.setEmail(obj.getEmail());
    }

    public Cliente fromDTO(@Valid ClienteNewDTO objDTO) {
        Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(),
                TipoCliente.toEnum(objDTO.getTipo()), pe.encode(objDTO.getSenha())); // passa o encode da senha
        Cidade cid = new Cidade(objDTO.getCidadeID(), null, null);
        Endereco end = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(),
                objDTO.getBairro(), objDTO.getCep(), cli, cid);
        cli.getEnderecos().add(end);
        cli.getTelefones().add(objDTO.getTelefone1());
        if (objDTO.getTelefone2() != null) {
            cli.getTelefones().add(objDTO.getTelefone2());
        }
        if (objDTO.getTelefone3() != null) {
            cli.getTelefones().add(objDTO.getTelefone3());
        }
        return cli;
    }

    public URI uploadProfilePicture(MultipartFile multipartFile) {
        UserSS user = UserService.authenticated();

        if (user == null) {
            throw new AuthorizationException("Acesso negado");
        }

        BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);
        String fileName = prefix + user.getId() + ".jpg";
        return s3service.uploadFile(imageService.getInputStream(jpgImage, "jpg"), fileName, "image");
    }
}
