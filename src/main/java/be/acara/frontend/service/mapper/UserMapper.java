package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserDto userModelToUserDto(UserModel userModel);
    
    UserModel userDtoToUserModel(UserDto userDto);
    
    User userDtoToUser(UserDto userDto);
    User userModelToUser(UserModel userModel);
}
