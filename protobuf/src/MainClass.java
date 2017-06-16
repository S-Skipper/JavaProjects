

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.proto.PersonProto;

public class MainClass {
	public static void main(String[] args) throws IOException {  
        
        // 按照定义的数据结构，创建一个Person  
		PersonProto.Person.Builder personBuilder = PersonProto.Person.newBuilder();  
        personBuilder.setName("test");  
        personBuilder.setAge(123);  
        personBuilder.setEmail("xxg@163.com");
        PersonProto.Person xxg = personBuilder.build();  
          
        // 将数据写到输出流，如网络输出流，这里就用ByteArrayOutputStream来代替  
        ByteArrayOutputStream output = new ByteArrayOutputStream();  
        xxg.writeTo(output);  
          
        // -------------- 分割线：上面是发送方，将数据序列化后发送 ---------------  
          
        byte[] byteArray = output.toByteArray();  
          
        // -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------  
          
        // 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替  
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);  
          
        // 反序列化  
        PersonProto.Person xxg2 = PersonProto.Person.parseFrom(input);  
        System.out.println("name:" + xxg2.getName());  
        System.out.println("age:" + xxg2.getAge()); 
        System.out.println("email:" + xxg2.getEmail());  
    }  
}
