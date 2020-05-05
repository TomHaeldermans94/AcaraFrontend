package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordConfirm", ignore = true)
    UserModel userDtoToUserModel(UserDto userDto);
    User userDtoToUser(UserDto userDto);
    
    UserDto userModelToUserDto(UserModel userModel);
    User userModelToUser(UserModel userModel);
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordConfirm", ignore = true)
    UserModel userToUserModel(User user);
    UserDto userToUserDto(User user);
}
