package com.hyf.protobuf.use;

import com.hyf.protobuf.test.AddressBook;
import com.hyf.protobuf.test.Person;

import java.io.*;

/**
 * @author baB_hyf
 * @date 2021/07/21
 */
public class Offical {

    static class AddPerson {
        // This function fills in a Person message based on user input.
        static Person PromptForAddress(BufferedReader stdin,
                                       PrintStream stdout) throws IOException {
            Person.Builder person = Person.newBuilder();

            stdout.print("Enter person ID: ");
            person.setId(Integer.valueOf(stdin.readLine()));

            stdout.print("Enter name: ");
            person.setName(stdin.readLine());

            stdout.print("Enter email address (blank for none): ");
            String email = stdin.readLine();
            if (email.length() > 0) {
                person.setEmail(email);
            }

            while (true) {
                stdout.print("Enter a phone number (or leave blank to finish): ");
                String number = stdin.readLine();
                if (number.length() == 0) {
                    break;
                }

                Person.PhoneNumber.Builder phoneNumber =
                        Person.PhoneNumber.newBuilder().setNumber(number);

                stdout.print("Is this a mobile, home, or work phone? ");
                String type = stdin.readLine();
                if (type.equals("mobile")) {
                    phoneNumber.setType(Person.PhoneType.MOBILE);
                }
                else if (type.equals("home")) {
                    phoneNumber.setType(Person.PhoneType.HOME);
                }
                else if (type.equals("work")) {
                    phoneNumber.setType(Person.PhoneType.WORK);
                }
                else {
                    stdout.println("Unknown phone type.  Using default.");
                }

                person.addPhones(phoneNumber);
            }

            return person.build();
        }

        // Main function:  Reads the entire address book from a file,
        //   adds one person based on user input, then writes it back out to the same
        //   file.
        public static void main(String[] args) throws Exception {
            args = new String[1];
            args[0] = "E:\\1.txt";
            if (args.length != 1) {
                System.err.println("Usage:  AddPerson ADDRESS_BOOK_FILE");
                System.exit(-1);
            }

            AddressBook.Builder addressBook = AddressBook.newBuilder();

            // Read the existing address book.
            try {
                addressBook.mergeFrom(new FileInputStream(args[0]));
            } catch (FileNotFoundException e) {
                System.out.println(args[0] + ": File not found.  Creating a new file.");
            }

            // Add an address.
            addressBook.addPerson(
                    PromptForAddress(new BufferedReader(new InputStreamReader(System.in)),
                            System.out));

            // Write the new address book back to disk.
            FileOutputStream output = new FileOutputStream(args[0]);
            addressBook.build().writeTo(output);
            output.close();
        }
    }

    static class ListPeople {
        // Iterates though all people in the AddressBook and prints info about them.
        static void Print(AddressBook addressBook) {
            for (Person person : addressBook.getPersonList()) {
                System.out.println("Person ID: " + person.getId());
                System.out.println("  Name: " + person.getName());
                if (person.hasEmail()) {
                    System.out.println("  E-mail address: " + person.getEmail());
                }

                for (Person.PhoneNumber phoneNumber : person.getPhonesList()) {
                    switch (phoneNumber.getType()) {
                        case MOBILE:
                            System.out.print("  Mobile phone #: ");
                            break;
                        case HOME:
                            System.out.print("  Home phone #: ");
                            break;
                        case WORK:
                            System.out.print("  Work phone #: ");
                            break;
                    }
                    System.out.println(phoneNumber.getNumber());
                }
            }
        }

        // Main function:  Reads the entire address book from a file and prints all
        //   the information inside.
        public static void main(String[] args) throws Exception {
            args = new String[1];
            args[0] = "E:\\1.txt";
            if (args.length != 1) {
                System.err.println("Usage:  ListPeople ADDRESS_BOOK_FILE");
                System.exit(-1);
            }

            // Read the existing address book.
            AddressBook addressBook =
                    AddressBook.parseFrom(new FileInputStream(args[0]));

            Print(addressBook);
        }
    }
}
