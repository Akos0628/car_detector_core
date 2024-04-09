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
        .button-container button {
             background-color: #007bff;
             color: #fff;
             border: none;
             padding: 10px 20px;
             cursor: pointer;
             margin-right: 10px;
         }
        .button-container button:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
        #preview {
            max-width: 100%;
            max-height: 70vh; /* Az ablak magasságának 70%-a */
            margin-bottom: 20px;
        }
        input[type="file"] {
            display: none;
        }
        .file-upload,
        input[type="submit"] {
            background-color: #007bff;
            color: #fff;
            border: none;
            padding: 10px 20px;
            cursor: pointer;
            margin-right: 10px;
        }
        input[type="submit"]:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
        .footer {
            position: fixed;
            bottom: 10px;
            left: 50%;
            transform: translateX(-50%);
            text-decoration: none;
            color: #007bff;
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
                document.querySelector('input[type=submit]').removeAttribute('disabled');
            } else {
                preview.src = "";
                document.querySelector('input[type=submit]').setAttribute('disabled', 'disabled');
            }
        }
    </script>
</head>
<body>
<div class="container">
    <h1>Car Detector</h1>
    <div class="form-container">
        <form action="/detector" method="post" enctype="multipart/form-data">
            <div class="button-container">
                <label for="file-upload" class="file-upload">Kép kiválasztása</label>
                <input id="file-upload" type="file" name="file" accept="image/jpeg" onchange="previewFile()">
            </div>
            <div class="button-container">
                <button type="submit">Küldés</button>
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
