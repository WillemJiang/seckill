package io.servicecomb.poc.demo.seckill;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.poc.demo.CommandServiceApplication;
import io.servicecomb.poc.demo.seckill.event.CouponEvent;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedCouponEventRepository;
import io.servicecomb.poc.demo.seckill.web.CouponCreate;
import io.servicecomb.poc.demo.seckill.web.CouponSecKill;
import io.servicecomb.poc.demo.seckill.web.CouponStart;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.MvcResult;
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

  @Autowired
  private SpringBasedCouponEventRepository repository;


  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
  }

  @Test
  public void testCreateAndSecKill() throws Exception {
    MvcResult result = this.mockMvc.perform(post("/admin/create").contentType(contentType).content(new ObjectMapper().writeValueAsString(new CouponCreate(5,0.7f,
        DateUtils.addSeconds(new Date(),2))))).andExpect(status().isOk()).andReturn();

    assertThat(result.getResponse().getContentLength(),is(36));

    String couponId = result.getResponse().getContentAsString();

    Thread.sleep(2000);

    this.mockMvc.perform(post("/admin/start").contentType(contentType).content(new ObjectMapper().writeValueAsString(new CouponStart(couponId))))
        .andExpect(status().isOk()).andExpect(content().string("true"));

    this.mockMvc.perform(post("/command/seckill").contentType(contentType).content(new ObjectMapper().writeValueAsString(new CouponSecKill("zyy"))))
        .andExpect(status().isOk()).andExpect(content().string("true"));

    this.mockMvc.perform(post("/command/seckill").contentType(contentType).content(new ObjectMapper().writeValueAsString(new CouponSecKill(10001))))
        .andExpect(status().isOk()).andExpect(content().string("true"));

    //one coupon only per user per promotion
    this.mockMvc.perform(post("/command/seckill").contentType(contentType).content(new ObjectMapper().writeValueAsString(new CouponSecKill("zyy"))))
        .andExpect(status().isOk()).andExpect(content().string("false"));
  }

  public void testRecovery() {
    repository.deleteAll();
    CouponInfo couponInfo = new CouponInfo(new Date(), 5, 0.7f);

    List<CouponEvent> events = new ArrayList<>();
    events.add(CouponEvent.genStartCouponEvent(couponInfo));
    events.add(CouponEvent.genSecKillCouponEvent(couponInfo,"zyy"));
    repository.save(events);


  }
}
