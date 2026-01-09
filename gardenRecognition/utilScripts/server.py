import os

from flask import Flask, request, jsonify

app = Flask(__name__)

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

app.config["MAX_CONTENT_LENGTH"] = 5 * 1024 * 1024  # 5 MB

ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg"}


def allowed_file(filename):
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route("/upload", methods=["POST"])
def upload_image():
    if "image" not in request.files:
        return jsonify({"error": "No image field"}), 400

    file = request.files["image"]

    if file.filename == "":
        return jsonify({"error": "Empty filename"}), 400

    if not allowed_file(file.filename):
        return jsonify({"error": "Invalid file type"}), 400

    filename = file.filename
    path = os.path.join(UPLOAD_DIR, filename)
    file.save(path)

    return jsonify({
        "status": "ok",
        "filename": filename
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)