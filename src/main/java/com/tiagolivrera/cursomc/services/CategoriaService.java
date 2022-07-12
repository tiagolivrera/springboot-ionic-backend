package com.tiagolivrera.cursomc.services;

import java.util.List;
import java.util.Optional;

import com.tiagolivrera.cursomc.domain.Categoria;
import com.tiagolivrera.cursomc.repositories.CategoriaRepository;
import com.tiagolivrera.cursomc.services.exceptions.DataIntegrityException;
import com.tiagolivrera.cursomc.services.exceptions.ObjectNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    public Categoria find(Integer id) {
        Optional<Categoria> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
    }

    public Categoria insert(Categoria obj) {
        obj.setId(null); // garantindo que o objeto a ser inserido é novo
        return repository.save(obj); // se id for diferente de null, save() vai tratar o obj como uma atualizacao, e
                                     // nao uma insercao
    }

    public Categoria update(Categoria obj) {
        find(obj.getId()); // verifica se o obj existe, caso contrario lanca uma excecao
        return repository.save(obj);
    }

    public void delete(Integer id) {
        find(id);
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) { // caso tente deletar uma categoria para a qual existe produtos, é
                                                      // lançada a exceção DataIntegrityViolationException
            throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos");

        }
    }

    public List<Categoria> findAll() {
        return repository.findAll();
    }

    // Paginacao: limita a quantidade de resultados entregues ao usuario -- util
    // para uma grande quantidade de dados
    public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
        return repository.findAll(pageRequest);
    }
}
