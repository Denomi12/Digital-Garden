import { Request, Response, NextFunction } from 'express';
import userController from '../src/controllers/userController';
import { generateToken } from '../src/utils/jwt';
import UserModel from '../src/models/UserModel';

// Popolnoma "mockamo" User model
// Pomembno je, da so metode "mockane" enako, kot se kličejo v kontrolerju (npr. User.find(), new User(), user.save())
const mockUser = {
  _id: 'someUserId123',
  username: 'testuser',
  email: 'test@example.com',
  password: 'hashedpassword',
  save: jest.fn().mockResolvedValue(this), // "Mock" za instančno metodo save
  toObject: jest.fn().mockReturnValue({
    // "Mock" za toObject
    _id: 'someUserId123',
    username: 'testuser',
    email: 'test@example.com',
    password: 'hashedpassword', 
  }),
  // Dodaj pogoste Mongoose lastnosti/metode, da ustreza tipu UserInstance
  isNew: false,
  $session: null, // Primer – lahko je bolj kompleksen glede na uporabo
  // Če ima tvoj UserModel metodo 'matchPassword' (pogosta pri avtentikaciji)
  matchPassword: jest.fn().mockResolvedValue(true), // "Mock" za preverjanje gesla
  // Dodaj druge lastnosti, ki jih kontroler morda uporablja,
} as any; // Uporabi 'as any' kot zadnjo možnost, če ne moreš zadovoljiti vseh Mongoose lastnosti

// Popolnoma "mockamo" modul 'UserModel'.
jest.mock('../src/models/UserModel', () => ({
  __esModule: true,
  default: jest.fn().mockImplementation(() => mockUser),
  // Izrecno definiraj statične metode kot jest mock funkcije
  find: jest.fn(),
  findById: jest.fn(),
  findOne: jest.fn(),
  findByIdAndDelete: jest.fn(),
  authenticate: jest.fn(),
}));

// Pretvori User v jest.Mocked<typeof UserModel>, da bodo statične metode pravilno tipizirane
const User = UserModel as jest.Mocked<typeof UserModel>;

// "Mockamo" funkcijo generateToken
jest.mock('../src/utils/jwt', () => ({
  generateToken: jest.fn(),
}));

// Nastavimo "mocke", da vedno vračajo določene vrednosti za testiranje
// To je pomembno, ker omogoča nadzor nad vedenjem "mockanih" funkcij.
(generateToken as jest.Mock).mockReturnValue('mocked-jwt-token');

// Pripravimo "mockane" Request in Response objekte
const mockRequest = {} as Request;
const mockResponse = {
  json: jest.fn(),
  status: jest.fn().mockReturnThis(),
  send: jest.fn(),
  cookie: jest.fn().mockReturnThis(),
  clearCookie: jest.fn().mockReturnThis(),
  locals: {
    user: {
      _id: 'loggedInUserId',
      username: 'loggedInUser',
      email: 'logged@example.com',
    },
  },
} as unknown as Response;


describe('userController', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('list', () => {
    it('should return all users successfully', async () => {
      const users = [
        { username: 'user1', email: 'u1@example.com' },
        { username: 'user2', email: 'u2@example.com' },
      ];
      User.find.mockResolvedValue(users);

      await userController.list(mockRequest, mockResponse);

      expect(User.find).toHaveBeenCalledTimes(1);
      expect(mockResponse.json).toHaveBeenCalledWith(users);
      expect(mockResponse.status).not.toHaveBeenCalled();
    });

    it('should handle errors when listing users', async () => {
      const errorMessage = 'Error when getting users';
      const error = new Error('Database error');
      User.find.mockRejectedValue(error);

      await userController.list(mockRequest, mockResponse);

      expect(User.find).toHaveBeenCalledTimes(1);
      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: errorMessage,
        error,
      });
    });
  });

  describe('show', () => {
    it('should return a user if found', async () => {
      mockRequest.params = { id: 'someUserId' };
      User.findById.mockResolvedValue(mockUser); // This is where the error occurred

      await userController.show(mockRequest, mockResponse);

      expect(User.findById).toHaveBeenCalledWith('someUserId');
      expect(mockResponse.json).toHaveBeenCalledWith(mockUser);
      expect(mockResponse.status).not.toHaveBeenCalled();
    });

    it('should return 404 if user not found', async () => {
      mockRequest.params = { id: 'nonExistentId' };
      User.findById.mockResolvedValue(null);

      await userController.show(mockRequest, mockResponse);

      expect(User.findById).toHaveBeenCalledWith('nonExistentId');
      expect(mockResponse.status).toHaveBeenCalledWith(404);
      expect(mockResponse.json).toHaveBeenCalledWith({ message: 'No such user' });
    });

    it('should handle errors when getting a user', async () => {
      mockRequest.params = { id: 'someUserId' };
      const errorMessage = 'Error when getting user';
      const error = new Error('Database error');
      User.findById.mockRejectedValue(error);

      await userController.show(mockRequest, mockResponse);

      expect(User.findById).toHaveBeenCalledWith('someUserId');
      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: errorMessage,
        error,
      });
    });
  });

  describe('create', () => {
    it('should create a new user and return a token', async () => {
      const newUserInput = {
        username: 'newUser',
        password: 'newPassword123',
        email: 'newuser@example.com',
      };
      mockRequest.body = newUserInput;

      User.findOne.mockResolvedValue(null);
      (mockUser.save as jest.Mock).mockResolvedValue({
        _id: 'newlyCreatedId',
        ...newUserInput,
        toObject: () => ({ _id: 'newlyCreatedId', ...newUserInput, password: 'hashedPassword' }),
      });

      await userController.create(mockRequest, mockResponse);

      expect(User.findOne).toHaveBeenCalledWith({
        $or: [{ username: newUserInput.username }, { email: newUserInput.email }],
      });
      expect(User).toHaveBeenCalledWith(newUserInput);
      expect(mockUser.save).toHaveBeenCalledTimes(1);
      expect(generateToken).toHaveBeenCalledWith({
        id: 'newlyCreatedId',
        username: newUserInput.username,
        email: newUserInput.email,
      });
      expect(mockResponse.cookie).toHaveBeenCalledWith('token', 'mocked-jwt-token', expect.any(Object));
      expect(mockResponse.status).toHaveBeenCalledWith(201);
      expect(mockResponse.json).toHaveBeenCalledWith({
        user: {
          _id: 'newlyCreatedId',
          username: newUserInput.username,
          email: newUserInput.email,
        },
      });
    });

    it('should return 400 if username or email already exists', async () => {
      const newUserInput = {
        username: 'existingUser',
        password: 'password123',
        email: 'existing@example.com',
      };
      mockRequest.body = newUserInput;

      User.findOne.mockResolvedValue(mockUser);

      await userController.create(mockRequest, mockResponse);

      expect(User.findOne).toHaveBeenCalledWith({
        $or: [{ username: newUserInput.username }, { email: newUserInput.email }],
      });
      expect(mockResponse.status).toHaveBeenCalledWith(400);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: 'User with this username or email already exists',
      });
      expect(User).not.toHaveBeenCalled();
      expect(mockUser.save).not.toHaveBeenCalled();
      expect(generateToken).not.toHaveBeenCalled();
    });

    it('should handle errors when creating a user', async () => {
      mockRequest.body = {
        username: 'test',
        password: 'password',
        email: 'test@test.com',
      };
      const errorMessage = 'Error when creating user';
      const error = new Error('Database error');
      User.findOne.mockResolvedValue(null);
      (mockUser.save as jest.Mock).mockRejectedValue(error);

      await userController.create(mockRequest, mockResponse);

      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: errorMessage,
        error,
      });
    });
  });

  describe('update', () => {
    it('should update a user if found', async () => {
      mockRequest.params = { id: 'someUserId' };
      mockRequest.body = { username: 'updatedUser', email: 'updated@example.com' };

      const userToUpdate = {
        ...mockUser,
        username: 'oldUser',
        email: 'old@example.com',
        save: jest.fn().mockResolvedValue({
          ...mockUser,
          username: 'updatedUser',
          email: 'updated@example.com',
        }),
      };
      User.findById.mockResolvedValue(userToUpdate);

      await userController.update(mockRequest, mockResponse);

      expect(User.findById).toHaveBeenCalledWith('someUserId');
      expect(userToUpdate.save).toHaveBeenCalledTimes(1);
      expect(mockResponse.json).toHaveBeenCalledWith({
        ...mockUser,
        username: 'updatedUser',
        email: 'updated@example.com',
      });
    });

    it('should return 404 if user not found for update', async () => {
      mockRequest.params = { id: 'nonExistentId' };
      mockRequest.body = { username: 'updatedUser' };
      User.findById.mockResolvedValue(null);

      await userController.update(mockRequest, mockResponse);

      expect(User.findById).toHaveBeenCalledWith('nonExistentId');
      expect(mockResponse.status).toHaveBeenCalledWith(404);
      expect(mockResponse.json).toHaveBeenCalledWith({ message: 'No such user' });
    });

    it('should handle errors when updating a user', async () => {
      mockRequest.params = { id: 'someUserId' };
      mockRequest.body = { username: 'updatedUser' };
      const errorMessage = 'Error when updating user';
      const error = new Error('Database error');

      const userToUpdate = { ...mockUser, save: jest.fn().mockRejectedValue(error) };
      User.findById.mockResolvedValue(userToUpdate);

      await userController.update(mockRequest, mockResponse);

      expect(User.findById).toHaveBeenCalledWith('someUserId');
      expect(userToUpdate.save).toHaveBeenCalledTimes(1);
      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: errorMessage,
        error,
      });
    });
  });

  describe('remove', () => {
    it('should delete a user successfully', async () => {
      mockRequest.params = { id: 'someUserId' };
      User.findByIdAndDelete.mockResolvedValue({});

      await userController.remove(mockRequest, mockResponse);

      expect(User.findByIdAndDelete).toHaveBeenCalledWith('someUserId');
      expect(mockResponse.status).toHaveBeenCalledWith(204);
      expect(mockResponse.send).toHaveBeenCalledTimes(1);
    });

    it('should handle errors when deleting a user', async () => {
      mockRequest.params = { id: 'someUserId' };
      const errorMessage = 'Error when deleting the user';
      const error = new Error('Database error');
      User.findByIdAndDelete.mockRejectedValue(error);

      await userController.remove(mockRequest, mockResponse);

      expect(User.findByIdAndDelete).toHaveBeenCalledWith('someUserId');
      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: errorMessage,
        error,
      });
    });
  });

  describe('login', () => {
    it('should login a user and return a token', async () => {
      mockRequest.body = { username: 'testuser', password: 'password123' };
      User.authenticate.mockResolvedValue(mockUser);

      await userController.login(mockRequest, mockResponse);

      expect(User.authenticate).toHaveBeenCalledWith('testuser', 'password123');
      expect(generateToken).toHaveBeenCalledWith({
        id: mockUser._id,
        username: mockUser.username,
        email: mockUser.email,
      });
      expect(mockResponse.cookie).toHaveBeenCalledWith('token', 'mocked-jwt-token', expect.any(Object));
      expect(mockResponse.json).toHaveBeenCalledWith({
        user: {
          _id: mockUser._id,
          username: mockUser.username,
          email: mockUser.email,
        },
      });
    });

    it('should return 400 if username or password are missing', async () => {
      mockRequest.body = { username: 'testuser' };

      await userController.login(mockRequest, mockResponse);

      expect(User.authenticate).not.toHaveBeenCalled();
      expect(mockResponse.status).toHaveBeenCalledWith(400);
      expect(mockResponse.json).toHaveBeenCalledWith({ message: 'Username and password are required' });
    });

    it('should return 401 if invalid credentials', async () => {
      mockRequest.body = { username: 'testuser', password: 'wrongpassword' };
      User.authenticate.mockResolvedValue(null);

      await userController.login(mockRequest, mockResponse);

      expect(User.authenticate).toHaveBeenCalledWith('testuser', 'wrongpassword');
      expect(generateToken).not.toHaveBeenCalled();
      expect(mockResponse.status).toHaveBeenCalledWith(401);
      expect(mockResponse.json).toHaveBeenCalledWith({ message: 'Invalid credentials' });
    });

    it('should handle internal server errors during login', async () => {
      mockRequest.body = { username: 'testuser', password: 'password123' };
      const error = new Error('Authentication failed');
      User.authenticate.mockRejectedValue(error);

      await userController.login(mockRequest, mockResponse);

      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.json).toHaveBeenCalledWith({ message: 'Internal server error' });
    });
  });

  describe('logout', () => {
    it('should clear the token cookie and return success message', async () => {
      await userController.logout(mockRequest, mockResponse);

      expect(mockResponse.clearCookie).toHaveBeenCalledWith('token', expect.any(Object));
      expect(mockResponse.status).toHaveBeenCalledWith(200);
      expect(mockResponse.json).toHaveBeenCalledWith({ message: 'Logged out successfully' });
    });
  });

  describe('me', () => {
    it('should return decoded user data from res.locals', async () => {
      await userController.me(mockRequest, mockResponse);

      expect(mockResponse.status).toHaveBeenCalledWith(200);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: 'You exist!',
        user: mockResponse.locals.user,
      });
    });
  });

  describe('verify', () => {
    it('should return user verification message and data from res.locals', async () => {
      await userController.verify(mockRequest, mockResponse);

      expect(mockResponse.status).toHaveBeenCalledWith(200);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: 'User verified',
        user: mockResponse.locals.user,
      });
    });
  });
});