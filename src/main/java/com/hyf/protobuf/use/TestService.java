package com.hyf.protobuf.use;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.hyf.protobuf.test.service.Request;
import com.hyf.protobuf.test.service.Response;
import com.hyf.protobuf.test.service.Service;

/**
 * @author baB_hyf
 * @date 2021/07/21
 */
public class TestService {

    public static void main(String[] args) {
        Descriptors.FileDescriptor descriptor = Service.getDescriptor();
        ExtensionRegistryLite extensionRegistryLite = ExtensionRegistryLite.newInstance();
        Service.registerAllExtensions(extensionRegistryLite);

        ExtensionRegistry emptyRegistry = ExtensionRegistry.getEmptyRegistry();
        Service.registerAllExtensions(emptyRegistry);

        Request request = Request.newBuilder()
                .buildPartial();

        Response response = Response.getDefaultInstance();

    }
}
