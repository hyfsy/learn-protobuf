// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package com.hyf.protobuf.test.service;

public interface ResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:java.test.Response)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 code = 1;</code>
   * @return The code.
   */
  int getCode();

  /**
   * <code>string text = 2;</code>
   * @return The text.
   */
  java.lang.String getText();
  /**
   * <code>string text = 2;</code>
   * @return The bytes for text.
   */
  com.google.protobuf.ByteString
      getTextBytes();

  /**
   * <code>string rtn = 3;</code>
   * @return The rtn.
   */
  java.lang.String getRtn();
  /**
   * <code>string rtn = 3;</code>
   * @return The bytes for rtn.
   */
  com.google.protobuf.ByteString
      getRtnBytes();
}