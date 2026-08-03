from fastapi import FastAPI, UploadFile, File
from pathlib import Path
from app.service.transcriber import transcribe

app = FastAPI()

MAX_CONCURRENT = 1
activeJobs = 0


@app.get("/capacity")
def capacity():
    return {
        "maxConcurrent": MAX_CONCURRENT,
        "running": activeJobs,
        "available": activeJobs < MAX_CONCURRENT
    }


@app.post("/transcribe")
async def transcribe_audio(file: UploadFile = File()):

    global activeJobs

    activeJobs += 1

    try:
        tempDir = Path("temp")
        tempDir.mkdir(exist_ok=True)

        tempFile = tempDir.joinpath(file.filename)

        contents = await file.read()
        tempFile.write_bytes(contents)

        result = transcribe(str(tempFile))

        return result

    finally:
        activeJobs -= 1
