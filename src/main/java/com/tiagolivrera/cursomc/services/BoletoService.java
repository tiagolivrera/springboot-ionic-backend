package com.tiagolivrera.cursomc.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.tiagolivrera.cursomc.domain.PagamentoComBoleto;

@Service
public class BoletoService {
    
    public void preencherPagamentoComBoleto(PagamentoComBoleto pagto, Date instanteDoPedido) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(instanteDoPedido);
        cal.add(Calendar.DAY_OF_MONTH, 7); // o boleto vence em 7 dias do pedido
        pagto.setDataVencimento(cal.getTime());
    }
}
