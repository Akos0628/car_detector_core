<!DOCTYPE html>
<html>
<head>
    <title>Post List</title>
    <style>
        /* Stílusok */
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
        }
        .post-container {
            margin-bottom: 20px;
            padding: 20px;
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 5px;
            overflow: hidden;
        }
        .post-image img {
            max-width: 100%;
            height: auto;
        }
        .post-description {
            margin-top: 20px;
            text-align: justify;
        }
        .upload-button-container {
            text-align: center;
            margin-bottom: 20px;
        }
        .upload-button {
            background-color: #007bff;
            color: #fff;
            border: none;
            padding: 10px 20px;
            cursor: pointer;
            border-radius: 5px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Post List</h1>
    <div class="upload-button-container">
        <button class="upload-button" onclick="window.location.href='/detector'">Feltöltés</button>
    </div>
    <#list posts as post>
        <div class="post-container">
            <div class="post-image">
                <img src="${post.imageUrl}" alt="Post image">
            </div>
            <div class="post-description">
                <p>${post.description}</p>
            </div>
        </div>
    </#list>
</div>
</body>
</html>
