<!DOCTYPE html>
<html>
<head>
    <title>Car Detector</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 40px 20px;
            text-align: center;
        }
        h1 {
            margin-bottom: 20px;
        }
        .form-container {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            margin-bottom: 20px;
        }
        .button-container {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 10px;
        }
        input[type=text], select {
            width: 100%;
            padding: 12px 20px;
            margin: 8px 0;
            display: inline-block;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .submit-container button {
            width: 100%;
            background-color: #4CAF50;
            color: white;
            padding: 14px 20px;
            margin: 8px 0;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .submit-container button:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
        .file-upload {
            width: 100%;
            background-color: #007bff;
            color: white;
            padding: 14px 20px;
            margin: 8px 0;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        #preview {
            max-width: 100%;
            max-height: 70vh; /* Az ablak magasságának 70%-a */
            margin-bottom: 20px;
        }
        input[type="file"] {
            display: none;
        }
        .footer {
            position: fixed;
            bottom: 10px;
            left: 50%;
            transform: translateX(-50%);
            text-decoration: none;
            color: #007bff;
        }
        .list-button {
            position: absolute;
            top: 10px;
            left: 10px;
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
        }
    </style>
    <script>
        function previewFile() {
            var preview = document.querySelector('img');
            var file = document.querySelector('input[type=file]').files[0];
            var reader = new FileReader();

            reader.onloadend = function () {
                preview.src = reader.result;
            }

            if (file) {
                reader.readAsDataURL(file);
                document.querySelector('button[type=submit]').removeAttribute('disabled');
            } else {
                preview.src = "";
                document.querySelector('button[type=submit]').setAttribute('disabled', 'disabled');
            }
        }
    </script>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
</head>
<body>
<div class="container">
    <h1>Car Detector</h1>
    <a href="/list" class="list-button">Vissza</a>
    <div class="form-container">
        <form action="/detector" method="post" enctype="multipart/form-data">
            <div class="button-container">
                <label for="file-upload" class="file-upload">Kép kiválasztása</label>
                <input id="file-upload" type="file" name="file" accept="image/jpeg" onchange="previewFile()">
            </div>
            <div>
                <input id="desc" type="text" name="description" placeholder="Leírás">
            </div>
            <div class="submit-container">
                <button type="submit" disabled>Feltöltés</button>
            </div>
        </form>
        <div>
            <img id="preview" src="${detectedImage}" alt="Kiválasztott kép">
        </div>
    </div>
</div>
<!-- Információs link -->
<a href="${operatorJoinUrl}" class="footer">Join our operators</a>
</body>
</html>
