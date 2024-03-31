package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.dao.IUserDao;
import com.openclassrooms.tourguide.dao.UserDaoImpl;

public class UserServiceFactory {

		public enum UserServiceMode{
			NORMAL,TEST
		};

		public static IUserDao create(UserServiceMode mode) {
			switch (mode) {
			case NORMAL:
				return new UserDaoImpl(false);
	
			case TEST:
				return new UserDaoImpl(true);
			}

			return new UserDaoImpl(false);
		}
}
