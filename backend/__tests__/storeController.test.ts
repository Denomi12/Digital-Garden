import { Request, Response } from 'express';
import storeController from '../src/controllers/storeController';
import StoreModel from '../src/models/StoreModel';

const mockStore = {
  _id: 'someStoreId123',
  key: 'testkey',
  name: 'Test Store',
  location: 'Test Location',
  latitude: 12.345,
  longitude: 67.890,
  save: jest.fn().mockResolvedValue(this), 
  toObject: jest.fn().mockReturnValue({
    _id: 'someStoreId123',
    key: 'testkey',
    name: 'Test Store',
    location: 'Test Location',
    latitude: 12.345,
    longitude: 67.890,
  }),
  isNew: false,
  $session: null,
} as any;

jest.mock('../src/models/StoreModel', () => ({
  __esModule: true,
  default: jest.fn().mockImplementation(() => mockStore),
  find: jest.fn(),
  findOne: jest.fn(),
}));

const Store = StoreModel as jest.Mocked<typeof StoreModel>;

const mockRequest = {} as Request;
const mockResponse = {
  json: jest.fn(),
  status: jest.fn().mockReturnThis(),
  send: jest.fn(),
} as unknown as Response;

describe('storeController', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('list', () => {
    it('should return all stores successfully', async () => {
      const stores = [
        { key: 's1', name: 'Store 1', location: 'Loc 1', latitude: 1, longitude: 1 },
        { key: 's2', name: 'Store 2', location: 'Loc 2', latitude: 2, longitude: 2 },
      ];
      Store.find.mockResolvedValue(stores);

      await storeController.list(mockRequest, mockResponse);

      expect(Store.find).toHaveBeenCalledTimes(1);
      expect(mockResponse.json).toHaveBeenCalledWith(stores);
      expect(mockResponse.status).not.toHaveBeenCalled();
    });

    it('should handle errors when listing stores', async () => {
      const errorMessage = 'Error when getting stores';
      const error = new Error('Database error');
      Store.find.mockRejectedValue(error);

      await storeController.list(mockRequest, mockResponse);

      expect(Store.find).toHaveBeenCalledTimes(1);
      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: errorMessage,
        error,
      });
    });
  });

  describe('create', () => {
    it('should create a new store successfully', async () => {
      const newStoreInput = {
        key: 'newstorekey',
        name: 'New Test Store',
        location: 'New Test Location',
        latitude: 98.765,
        longitude: 43.210,
      };
      mockRequest.body = newStoreInput;

      (mockStore.save as jest.Mock).mockResolvedValue({
        _id: 'newlyCreatedStoreId',
        ...newStoreInput,
      });

      await storeController.create(mockRequest, mockResponse);

      expect(Store).toHaveBeenCalledWith(newStoreInput);
      expect(mockStore.save).toHaveBeenCalledTimes(1);
      expect(mockResponse.status).toHaveBeenCalledWith(201);
      expect(mockResponse.json).toHaveBeenCalledWith({
        _id: 'newlyCreatedStoreId',
        ...newStoreInput,
      });
    });

    it('should handle errors when creating a store', async () => {
      mockRequest.body = {
        key: 'invalid',
        name: 'Invalid Store',
        location: 'Invalid Location',
      };
      const errorMessage = 'Failed to create store';
      const error = new Error('Validation error');
      (mockStore.save as jest.Mock).mockRejectedValue(error);

      await storeController.create(mockRequest, mockResponse);

      expect(mockStore.save).toHaveBeenCalledTimes(1);
      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.json).toHaveBeenCalledWith({
        message: errorMessage,
        error,
      });
    });
  });
});