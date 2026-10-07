package com.kaisernova.dao.producto;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.producto.Bien;

public class BienDao extends GenericActivableDao<Bien, Long> {

    public BienDao() {
        super(Bien.class);
    }

}
