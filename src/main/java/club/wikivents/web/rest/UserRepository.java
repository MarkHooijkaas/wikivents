package club.wikivents.web.rest;

import club.wikivents.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserRepository implements Repository<String, User> {

    private Map<String, User> userMap = new TreeMap<>();

    @Override
    public User getById(String id) {
        User user = findById(id);
        if (null == user) {
            throw new IllegalArgumentException("Unknown id: " + id);
        } else {
            return user;
        }
    }

    @Override
    public User findById(String id) {
        return userMap.get(id);
    }

    @Override
    public List<User> listAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public String create(User user) {
        String userId = user._id;
        if (null == userId || userMap.containsKey(userId)) {
            throw new IllegalStateException("User already exists with id: " + userId);
        } else {
            userMap.put(userId, user);
            return userId;
        }
    }

    @Override
    public void delete(String userId) {
        User removedUser = userMap.remove(userId);
        if (removedUser == null) {
            throw new IllegalStateException("User does not exist for id: " + userId);
        }
    }
}
