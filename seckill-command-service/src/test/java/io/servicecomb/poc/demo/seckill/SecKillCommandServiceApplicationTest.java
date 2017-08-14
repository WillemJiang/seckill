package io.servicecomb.poc.demo.seckill;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.CommandServiceApplication;
import io.servicecomb.poc.demo.seckill.web.CouponSecKill;
import io.servicecomb.poc.demo.seckill.web.CouponStart;
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
@SpringBootTest(classes = CommandServiceApplication.class)
@WebAppConfiguration
public class SecKillCommandServiceApplicationTest {

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
  public void testCreateAndSecKill() throws Exception {
    this.mockMvc.perform(post("/admin/start").contentType(contentType).content(new ObjectMapper().writeValueAsString(new CouponStart(5,0.7f))))
        .andExpect(status().isOk()).andExpect(content().string("true"));

    this.mockMvc.perform(post("/command/seckill").contentType(contentType).content(new ObjectMapper().writeValueAsString(new CouponSecKill("zyy"))))
        .andExpect(status().isOk()).andExpect(content().string("true"));
  }
}
