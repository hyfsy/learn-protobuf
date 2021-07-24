package com.hyf.protobuf.use;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.hyf.protobuf.test.Person;

/**
 * @author baB_hyf
 * @date 2021/07/24
 */
public class JsonConvert {

    public static void main(String[] args) {
        Person person = Person.newBuilder().setId(1)
                .setName("张三")
                .addPhones(Person.PhoneNumber.newBuilder()
                        .setType(Person.PhoneType.HOME)
                        .setNumber("111-111-111")
                        .build())
                .build();

        System.out.println(person);

        try {
            System.out.println("=============================");
            String json = JsonFormat.printer().print(person);
            System.out.println(json);
            System.out.println("=============================");

            Person.Builder builder = Person.newBuilder();
            JsonFormat.parser().merge(json, builder);
            Person parsedPerson = builder.build();

            System.out.println(parsedPerson);
            System.out.println("=============================");
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }
}
