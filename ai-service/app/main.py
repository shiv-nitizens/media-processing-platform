from fastapi import FastAPI , UploadFile , File
from pathlib import Path
from app.service.transcriber import transcribe

app = FastAPI()

@app.post("/transcribe")
async def transcribe_audio(file: UploadFile = File()):
    
    tempDir = Path("temp")
    tempDir.mkdir(exist_ok=True)
    tempFile = tempDir.joinpath(file.filename)
    contents = await file.read()
    tempFile.write_bytes(contents)
    result = transcribe(str(tempFile))

    return result
