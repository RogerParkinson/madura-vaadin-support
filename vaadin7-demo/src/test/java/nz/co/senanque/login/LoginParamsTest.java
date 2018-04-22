package nz.co.senanque.login;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=LoginConfigure.class, loader=AnnotationConfigContextLoader.class)
public class LoginParamsTest {

	@Autowired LoginParams loginParams;

	@Test
	public void test() {
		loginParams.getKeyPairName();
	}

}
