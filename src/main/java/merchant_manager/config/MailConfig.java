package merchant_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.email.us-ashburn-1.oci.oraclecloud.com");
        mailSender.setPort(587);
        mailSender.setUsername("ocid1.user.oc1..aaaaaaaanpizqv33s2hbss52zi46plfaqgqysro4qowvvjptqmpd4yfikcaa@ocid1.tenancy.oc1..aaaaaaaaooamblksdgqcxljlezuu72qcywjk5n2gp2zcvi7m4dxff3oqoa3a.p0.com");
        mailSender.setPassword("7o5d-pJ1P;Rpq{Trx.KJ");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return mailSender;
    }
}
