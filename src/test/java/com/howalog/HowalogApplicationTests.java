package com.howalog;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.howalog.domain.QPost.post;

@SpringBootTest
class HowalogApplicationTests {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Test
    void contextLoads() {
    }

}
