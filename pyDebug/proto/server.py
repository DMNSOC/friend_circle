from flask import Flask, request, Response, send_file
import sqlite3
import user_pb2
import os
import uuid


app = Flask(__name__)
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

DB_FILE = "users.db"
DB_INFO = "info.db"

def init_db():
    conn = sqlite3.connect(DB_FILE)
    c = conn.cursor()
    c.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            useId INTEGER NOT NULL,
            decStr TEXT NOT NULL,
            friendImageId TEXT NOT NULL,
            timeStr TEXT NOT NULL,
            friendVideoId TEXT NOT NULL,
            friendVideoTime TEXT NOT NULL,
            likesId TEXT NOT NULL
        )
    ''')
    c.execute('''
        CREATE TABLE IF NOT EXISTS info (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            useId INTEGER NOT NULL,
            friendName TEXT NOT NULL,
            friendHead TEXT NOT NULL,
            friendBg TEXT NOT NULL
        )
    ''')
    conn.commit()
    conn.close()

#     conf = sqlite3.connect(DB_INFO)
#     d = conf.cursor()
#     d.execute('''
#         CREATE TABLE IF NOT EXISTS info (
#             id INTEGER PRIMARY KEY AUTOINCREMENT,
#             useId INTEGER NOT NULL,
#             friendName TEXT NOT NULL,
#             friendHead TEXT NOT NULL,
#             friendBg TEXT NOT NULL
#         )
#     ''')
#     conf.commit()
#     conf.close()

@app.route('/create_user', methods=['POST'])
def create_user():
    try:
        user = user_pb2.User()
        user.ParseFromString(request.data)
        print(f"收到请求信息 ： {user.useId}")
        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()
        c.execute("INSERT INTO users (useId, decStr, friendImageId, timeStr, friendVideoId, friendVideoTime, likesId) VALUES (?, ?, ?, ?, ?, ?, ?)",(user.useId, user.decStr, user.friendImageId, user.timeStr, user.friendVideoId, user.friendVideoTime, user.likesId))
        conn.commit()
        user_id = c.lastrowid
        conn.close()

        uid = user_pb2.UserId(id=user_id)
        return Response(uid.SerializeToString(), mimetype='application/octet-stream')
    except Exception as e:
        print(e)
        return Response("error", status=400)
    
@app.route('/list_users', methods=['POST'])
def list_users():
    try:
        empty = user_pb2.Empty()
        empty.ParseFromString(request.data)
        print(f"收到请求列表信息")
        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()
        c.execute("SELECT id, useId, decStr, friendImageId, timeStr, friendVideoId, friendVideoTime, likesId FROM users")
        users = user_pb2.UserList()
        for row in c.fetchall():
            u = users.users.add()
            u.id = row[0]
            u.useId = row[1]
            u.decStr = row[2]
            u.friendImageId = row[3]
            u.timeStr = row[4]
            u.friendVideoId = row[5]
            u.friendVideoTime = row[6]
            u.likesId = row[7]
        conn.close()
        return Response(users.SerializeToString(), mimetype='application/octet-stream')
    except Exception as e:
        print(e)
        return Response("error", status=400)

@app.route('/update_user', methods=['POST'])
def update_user():
    try:
        req = user_pb2.UpdateUserRequest()
        req.ParseFromString(request.data)
        print(f"收到请求修改信息")
        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()
        c.execute("UPDATE users SET likesId = ? WHERE id = ?", (req.likesId, req.id))
        conn.commit()
        success = c.rowcount > 0
        conn.close()
        result = user_pb2.BoolResult(success=success)
        status = 200 if success else 404
        return Response(result.SerializeToString(), mimetype='application/octet-stream', status=status)
    except Exception as e:
        print(e)
        return Response("error", status=400)





@app.route('/create_info', methods=['POST'])
def create_info():
    try:
        info = user_pb2.Info()
        info.ParseFromString(request.data)
        print(f"创建用户信息 ： {info.useId}")
        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()
        c.execute("SELECT id, useId, friendName, friendHead, friendBg FROM info WHERE useId = ?", (info.useId,))
        row = c.fetchone()
        if row:
            info_id = c.lastrowid
            conn.close()
            uid = user_pb2.InfoId(id=info_id)
            return Response(uid.SerializeToString(), mimetype='application/octet-stream')
        else:
            c = conn.cursor()
            c.execute("INSERT INTO info (useId, friendName, friendHead, friendBg) VALUES (?, ?, ?, ?)",(info.useId, info.friendName, info.friendHead, info.friendBg))
            conn.commit()
            info_id = c.lastrowid
            conn.close()
            uid = user_pb2.InfoId(id=info_id)
            return Response(uid.SerializeToString(), mimetype='application/octet-stream')

    except Exception as e:
        print(e)
        return Response("error", status=400)

@app.route('/list_info', methods=['POST'])
def list_info():
    try:
        empty = user_pb2.Empty()
        empty.ParseFromString(request.data)
        print(f"收到请求用户信息列表")
        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()
        c.execute("SELECT id, useId, friendName, friendHead, friendBg FROM info")
        users = user_pb2.InfoList()
        for row in c.fetchall():
            u = users.infos.add()
            u.id = row[0]
            u.useId = row[1]
            u.friendName = row[2]
            u.friendHead = row[3]
            u.friendBg = row[4]
        conn.close()
        return Response(users.SerializeToString(), mimetype='application/octet-stream')
    except Exception as e:
        print(e)
        return Response("error", status=400)

@app.route('/update_info', methods=['POST'])
def update_info():
    try:
        req = user_pb2.Info()
        req.ParseFromString(request.data)
        print(f"收到请求修改信息")
        conn = sqlite3.connect(DB_FILE)
        conn.row_factory = sqlite3.Row
        c = conn.cursor()
        c.execute("SELECT id, useId, friendName, friendHead, friendBg FROM info WHERE useId = ?", (req.useId,))
        row = c.fetchone()
        if not row:
            conn.close()
            return Response(user_pb2.Info().SerializeToString(), mimetype='application/octet-stream', status=404)

        new_friendName = req.friendName if req.HasField("friendName") else row["friendName"]
        new_friendHead = req.friendHead if req.HasField("friendHead") else row["friendHead"]
        new_friendBg = req.friendBg if req.HasField("friendBg") else row["friendBg"]

        c.execute("UPDATE info SET friendName = ?, friendHead = ?, friendBg = ? WHERE useId = ?", (new_friendName, new_friendHead, new_friendBg, req.useId))
        conn.commit()

        c.execute("SELECT id, useId, friendName, friendHead, friendBg FROM info WHERE useId = ?", (req.useId,))
        row = c.fetchone()
        user = user_pb2.Info(id=row[0], useId=row[1], friendName=row[2], friendHead=row[3], friendBg=row[4])
        conn.close()
        return Response(user.SerializeToString(), mimetype='application/octet-stream')
    except Exception as e:
        print(e)
        return Response("error", status=400)

@app.route('/delete_user', methods=['POST'])
def delete_user():
    try:
        req = user_pb2.DeleteUserRequest()
        req.ParseFromString(request.data)
        print(f"收到请求删除信息")
        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()
        c.execute("DELETE FROM users WHERE id = ?", (req.id,))
        conn.commit()
        success = c.rowcount > 0
        conn.close()
        result = user_pb2.BoolResult(success=success)
        status = 200 if success else 404
        return Response(result.SerializeToString(), mimetype='application/octet-stream', status=status)
    except Exception as e:
        print(e)
        return Response("error", status=400)

@app.route('/batch_upload_media', methods=['POST'])
def batch_upload_media():
    req = user_pb2.BatchMediaUploadRequest()
    req.ParseFromString(request.data)
    print(f"收到储存资源")
    resp = user_pb2.BatchMediaUploadResponse()

    for f in req.files:
        result = resp.results.add()
        result.filename = f.filename
        if not f.filename or not f.data:
            result.success = False
            result.error_msg = "Missing filename or data"
            continue
        ext = os.path.splitext(f.filename)[-1]
        media_id = str(uuid.uuid4())
        save_name = f"{media_id}{ext}"
        path = os.path.join(UPLOAD_DIR, save_name)
        try:
            with open(path, "wb") as file:
                file.write(f.data)
            result.media_id = media_id
            result.url = f"/media/{save_name}"
            result.success = True
        except Exception as e:
            result.success = False
            result.error_msg = str(e)

    return Response(resp.SerializeToString(), mimetype="application/octet-stream")

@app.route('/media/<filename>', methods=['GET'])
def get_media(filename):
    path = os.path.join(UPLOAD_DIR, filename)
    if not os.path.isfile(path):
        return "资源为空", 404
    return send_file(path)


if __name__ == "__main__":
    init_db()
    app.run(host='0.0.0.0', port=5000, debug=True)