package com.finatiol.productos;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class FinatiolProductosMsApplicationTests {

	@MockBean
	MinioClient minioClient;

	@Test
	void contextLoads() {
	}

}
