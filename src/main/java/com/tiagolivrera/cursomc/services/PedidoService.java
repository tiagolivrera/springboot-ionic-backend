package com.tiagolivrera.cursomc.services;

import java.util.Optional;

import com.tiagolivrera.cursomc.domain.Pedido;
import com.tiagolivrera.cursomc.repositories.PedidoRepository;
import com.tiagolivrera.cursomc.services.exceptions.ObjectNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    public Pedido find(Integer id) {
        Optional<Pedido> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
            "Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()
        ));
    }
}
