package com.hyf.protobuf.use;

import com.google.protobuf.*;
import com.hyf.protobuf.test.AddressBook;
import com.hyf.protobuf.test.AddressBookOuterClass;
import com.hyf.protobuf.test.Person;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/07/21
 */
public class TestApi {

    public static void main(String[] args) throws IOException {
        // Person.parseDelimitedFrom()
        Person defaultInstance = Person.getDefaultInstance();
        System.out.println(defaultInstance.toString());

        Descriptors.Descriptor descriptor = Person.getDescriptor();
        System.out.println(descriptor);

        Parser<Person> parser = Person.parser();
        // parser.parsePartialFrom(null);

        Person defaultInstanceForType = defaultInstance.getDefaultInstanceForType();
        System.out.println(defaultInstance.equals(defaultInstanceForType));
        System.out.println(defaultInstance.isInitialized());
        ByteString emailBytes = defaultInstance.getEmailBytes();

        List<? extends Person.PhoneNumberOrBuilder> phonesOrBuilderList = defaultInstance.getPhonesOrBuilderList();
        System.out.println(phonesOrBuilderList);

        System.out.println(defaultInstance.getSerializedSize());

        UnknownFieldSet unknownFields = defaultInstance.getUnknownFields();
        System.out.println(unknownFields);

        Person.Builder builder = defaultInstance.newBuilderForType();

        Person.Builder builder1 = defaultInstance.toBuilder();

        Parser<Person> parserForType = defaultInstance.getParserForType();

        defaultInstance.writeDelimitedTo(null);
        List<String> initializationErrors = defaultInstance.findInitializationErrors();
        Map<Descriptors.FieldDescriptor, Object> allFields = defaultInstance.getAllFields();
        Descriptors.Descriptor descriptorForType = defaultInstance.getDescriptorForType();
        defaultInstance.getField(null);
        String initializationErrorString = defaultInstance.getInitializationErrorString();
        Descriptors.FieldDescriptor oneofFieldDescriptor = defaultInstance.getOneofFieldDescriptor(null);
        Object repeatedField = defaultInstance.getRepeatedField(null, 0);
        boolean b = defaultInstance.hasField(null);
        boolean b1 = defaultInstance.hasOneof(null);
        ByteString bytes = defaultInstance.toByteString();

        CodedInputStream codedInputStream1 = CodedInputStream.newInstance(null, 0, 0);

        Person person = builder.addRepeatedField(null, 0)
                .clearEmail()
                .clearField(null)
                .clearOneof(null)
                .clone()
                .mergeUnknownFields(null)
                .setRepeatedField(null, 0, null)
                .buildPartial();

        DescriptorProtos.DescriptorProto descriptorProto = descriptorForType.toProto();

    }
}
