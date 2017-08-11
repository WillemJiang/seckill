package io.servicecomb.poc.demo.seckill;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import io.servicecomb.poc.demo.CommandQueryApplication;
import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.event.CouponEventType;
import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import java.nio.charset.Charset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandQueryApplication.class)
@WebAppConfiguration
public class SecKillQueryServiceApplicationTest {

  @Autowired
  private CouponEventRepository repository;

  private MockMvc mockMvc;

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
  }

  @Test
  public void testQueryCurrent() throws Exception {
    repository.deleteAll();
    CouponEvent event = new CouponEvent(CouponEventType.Start,10,(float)0.7);
    repository.save(event);
    repository.flush();

    this.mockMvc.perform(get("/query/current").contentType(contentType))
        .andExpect(status().isOk()).andExpect(content().string(containsString(event.getId())));
  }

  @Test
  public void testQuerySuccess() throws Exception {
    repository.deleteAll();
    CouponEvent event = new CouponEvent(CouponEventType.SecKill,"zyy",1,(float)0.7);
    repository.save(event);
    repository.flush();

    this.mockMvc.perform(get("/query/success/zyy").contentType(contentType))
        .andExpect(status().isOk()).andExpect(content().string(containsString(event.getId())));
  }
}
