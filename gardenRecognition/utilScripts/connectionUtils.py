import requests


class GardenConnection:
    def __init__(self, username: str, password: str):
        self.username = username
        self.password = password
        self.URL = "http://localhost:3001"
        self.session = requests.Session()


    def login(self):
        data = {"username": self.username, "password": self.password}

        endpoint = self.URL + "/user/login"
        r = self.session.post(url=endpoint, data=data)
        cookie = r.cookies.get("token")
        return r.json(), cookie

    def verify_user(self):
        endpoint = self.URL + "/user/me"

        r = self.session.get(url=endpoint)
        return r.json()

    def post_garden(self, garden_data: dict):
        endpoint = self.URL + "/garden"
        headers = {"Content-Type": "application/json"}
        r = self.session.post(url=endpoint, headers=headers, json=garden_data)
        return r.json()


if __name__ == "__main__":
    pass