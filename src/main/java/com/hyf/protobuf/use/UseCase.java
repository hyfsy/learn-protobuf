package com.hyf.protobuf.use;


import com.hyf.protobuf.test.Person;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author baB_hyf
 * @date 2021/07/21
 */
public class UseCase {

    public static void main(String[] args) throws Exception {

        Person person = Person.newBuilder().setId(1).setEmail("11111@qq.com")
                .addPhones(Person.PhoneNumber.newBuilder()
                        .setNumber("111-111-111-111")
                        .setType(Person.PhoneType.HOME))
                .build();

        boolean initialized = person.isInitialized();
        System.out.println("initialized: " + initialized);
        String s = person.toString();
        System.out.println("person string: \n" + s);
        byte[] bytes = person.toByteArray();

        person.writeTo(new FileOutputStream("E://1.txt"));
        Person.parseFrom(bytes);
        Person.parseFrom(new FileInputStream("E://1.txt"));

        Person.Builder builder = person.toBuilder();
        builder.mergeFrom(Person.newBuilder().build());
        builder.clear();

    }
}
