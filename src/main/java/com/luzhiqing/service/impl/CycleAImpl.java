package com.luzhiqing.service.impl;

import com.luzhiqing.service.CycleA;
import com.luzhiqing.service.CycleB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: luzhiqing
 * @date: 2021/3/18
 * @version:
 */
@Service
public class CycleAImpl implements CycleA {
    @Autowired
    private CycleB cycleB;
    @Override
    //@Async
    @Transactional
    public void print() {
        cycleB.print();
    }
}
