import os
import torch
import numpy as np
import torch.nn.functional as F
import pretrainedmodels as pre
import pretrainedmodels.utils as utils

if not os.path.exists(os.path.join(os.path.dirname(os.path.realpath(__file__)),"model.txt")):
    model=pre.pnasnet5large(1000)
    torch.save(model, "model.txt")

classes=np.array(open('imagenet_classes.txt','r').read().split('\n'))

device=("cuda:0" if torch.cuda.is_available() else "cpu")
print ("Using "+device)

model=torch.load('model.txt').to(device)
model.eval()

def prep_image(img,model,device):
    # Load and Transform one input image
    tf_img = utils.TransformImage(model)
    input_data = tf_img(img)
    input_data = input_data.unsqueeze(0)
    return input_data.to(device)

def classify(img,model=model,device=device):
    img=prep_image(img,model,device)
    output=model.forward(img)
    return classes[torch.argmax(output)]

def classify_in_memory(path_img,model=model,device=device):
    load_img=utils.LoadImage()
    input_data=prep_image(load_img(path_img),model,device)
    return classify(input_data,model)

if __name__=='__main__':
    print (classify_in_memory('./test/kora2.jpg'))
