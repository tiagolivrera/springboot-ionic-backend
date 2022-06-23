package com.tiagolivrera.cursomc.services;

import java.util.Optional;

import com.tiagolivrera.cursomc.domain.Cliente;
import com.tiagolivrera.cursomc.repositories.ClienteRepository;
import com.tiagolivrera.cursomc.services.exceptions.ObjectNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public Cliente find(Integer id) {
        Optional<Cliente> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
            "Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()
        ));
    }
}
