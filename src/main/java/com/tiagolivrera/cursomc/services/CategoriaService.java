package com.tiagolivrera.cursomc.services;

import java.util.Optional;

import com.tiagolivrera.cursomc.domain.Categoria;
import com.tiagolivrera.cursomc.repositories.CategoriaRepository;
import com.tiagolivrera.cursomc.services.exceptions.ObjectNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
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
}
