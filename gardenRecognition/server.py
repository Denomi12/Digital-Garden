import os

from flask import Flask, request, jsonify

from utilScripts import load_image, extract_garden, create_tilemap, blocks_to_elements, create_garden, GardenConnection
app = Flask(__name__)

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

app.config["MAX_CONTENT_LENGTH"] = 5 * 1024 * 1024  # 5 MB

ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg"}


def allowed_file(filename):
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route("/", methods=["GET"])
def helllo():
    return jsonify({"Hello": "You exist!"}), 200


@app.route("/upload", methods=["POST"])
def upload_image():
    print("Received POST /upload")
    if "image" not in request.files:
        print({"error": "No image field"})
        return jsonify({"error": "No image field"}), 400

    file = request.files["image"]

    try:
        username, password = request.form["username"], request.form["password"]
    except KeyError:
        print({"error": "No username or password field"})
        return jsonify({"error": "No username or password field"}), 400

    if file.filename == "":
        print({"error": "Empty filename"})
        return jsonify({"error": "Empty filename"}), 400

    if not allowed_file(file.filename):
        print({"error": "Invalid file type"})
        return jsonify({"error": "Invalid file type"}), 400

    conn = GardenConnection(username, password)
    print("Establishing connection with USER")
    try:
        conn.login()

    except Exception as e:
        print({"error": f"Login error: {e}"})
        return jsonify({"error": "Login error"}), 400

    filename = file.filename
    garden_name = (filename.split(".")[0])
    path = os.path.join(UPLOAD_DIR, filename)
    file.save(path)

    print("Saving image file")

    response = process_image(conn, path, garden_name)
    print("Sent response: ", response)
    return jsonify({
        "status": "ok",
        "filename": filename,
        "response": response
    })

def process_image(conn: GardenConnection, path, garden_name):
    garden_image = load_image(path)

    garden, mask, contour = extract_garden(garden_image)
    tilemap = create_tilemap(mask, height=20, width=40)
    elements = blocks_to_elements(tilemap)

    garden_json = create_garden(name=garden_name, elements=elements)
    res = conn.post_garden(garden_json)
    return res

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)