package com.flyer.service;

import com.flyer.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    @Qualifier("bookDao")
    @Autowired(required = false)
    private BookDao bookDao;

    public void printBookDao() {
        System.out.println(bookDao);
    }
}
