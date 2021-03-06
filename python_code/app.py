import os
from flask import Flask, flash, request, redirect, url_for, send_from_directory
from werkzeug.utils import secure_filename
import classifier
from PIL import Image

ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg'])

app = Flask(__name__)
app.secret_key=os.urandom(16)

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        print ('hi')
        # check if the post request has the file part
        if 'Image.jpg' not in request.files:
            print ('No file part')
            flash('No file part')
            return redirect(request.url)
        file = request.files['Image.jpg']

        # if user does not select file, browser also
        # submit an empty part without filename
        if file.filename == '':
            print ('No selected file')
            flash('No selected file')
            return redirect(request.url)

        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)

            try:
                img = Image.open(request.files['Image.jpg'].stream)
                return classifier.classify(img)

            except Exception as e:
                print (e)


    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <input type=file name=file>
      <input type=submit value=Upload>
    </form>
    '''

if __name__ == '__main__':
   app.run(host='0.0.0.0',debug=True)
