package br.com.rraminelli.moviesbattle.game.client;

import br.com.rraminelli.moviesbattle.game.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("userauth")
public interface UserClient {

    @RequestMapping(value = "/user/current-user", method = RequestMethod.GET)
    UserDto getCurrentUser();

}
