package com.tiagolivrera.cursomc.services;

import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import com.tiagolivrera.cursomc.domain.ItemPedido;
import com.tiagolivrera.cursomc.domain.PagamentoComBoleto;
import com.tiagolivrera.cursomc.domain.Pedido;
import com.tiagolivrera.cursomc.domain.enums.EstadoPagamento;
import com.tiagolivrera.cursomc.repositories.ItemPedidoRepository;
import com.tiagolivrera.cursomc.repositories.PagamentoRepository;
import com.tiagolivrera.cursomc.repositories.PedidoRepository;
import com.tiagolivrera.cursomc.services.exceptions.ObjectNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private EmailService emailService;

    public Pedido find(Integer id) {
        Optional<Pedido> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
            "Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()
        ));
    }

    @Transactional
    public @Valid Pedido insert(@Valid Pedido obj) {
        obj.setId(null); // garante que estou inserindo um novo pedido na base de dados
        obj.setInstante(new Date()); // insere o instante atual
        obj.setCliente(clienteService.find(obj.getCliente().getId())); // busca o cliente na base de dados com base no id
        obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
        obj.getPagamento().setPedido(obj); // o pedido deve conhecer o pagamento e vice versa (OneToOne)
        if (obj.getPagamento() instanceof PagamentoComBoleto) {
            PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
            boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
        }

        obj = repository.save(obj);
        pagamentoRepository.save(obj.getPagamento());
        for (ItemPedido ip : obj.getItens()) {
            ip.setDesconto(0.0);
            ip.setProduto(produtoService.find(ip.getProduto().getId()));
            ip.setPreco(ip.getProduto().getPreco()); // coloca no itempedido os precos do produto
            ip.setPedido(obj);
        }
        itemPedidoRepository.saveAll(obj.getItens());
        emailService.sendOrderConfirmationEmail(obj);
        return obj;
    }
}