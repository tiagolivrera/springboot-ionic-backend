package com.tiagolivrera.cursomc.repositories;

import com.tiagolivrera.cursomc.domain.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    @Transactional(readOnly = true) /*
                                     * readonly=true -- operacao nao necessita de ser envolvida como uma transacao
                                     * do banco de dados, ficando mais rapido e diminuindo o locking no
                                     * gerenciamento de operacoes do banco de dados
                                     */
    Cliente findByEmail(String email); // o Spring Data implementa esse metodo automaticamente

}
