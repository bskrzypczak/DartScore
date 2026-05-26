from roboflow import Roboflow

rf = Roboflow(api_key="Yym3kANLNjgbvyz8Pthm")
project = rf.workspace("score-lpfmv").project("darts-bjj98")
dataset = project.version(1).download("yolov8")