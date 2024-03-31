package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.service.UserService;

public class UserServiceFactory {

		public enum UserServiceMode{
			NORMAL,TEST
		};

		public static UserService create(UserServiceMode mode) {
			switch (mode) {
			case NORMAL:
				return new UserService(new UserDaoImpl(false));
	
			case TEST:
				return new UserService(new UserDaoImpl(true));
			}

			return new UserService(new UserDaoImpl(false));
		}
}
