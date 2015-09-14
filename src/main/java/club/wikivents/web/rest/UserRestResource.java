package club.wikivents.web.rest;

import club.wikivents.model.User;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserRestResource {

    public final UserRepository userRepository;

    public UserRestResource(UserRepository userRepository) {
        Objects.nonNull(userRepository);
        this.userRepository = userRepository;
    }

    @GET
    public List<User> list() {
        return userRepository.listAll();
    }

    @GET
    @Path("{userId}")
    public User find(@PathParam("userId") String userId) {
        return userRepository.findById(userId);
    }

}
