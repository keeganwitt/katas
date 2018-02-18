# modified version of https://gist.github.com/alexmic/8345076
# TODO: is there a way to do this in Java so we can do on the fly (maybe with JMagick, Marvin, ImageJ, or plain Java Advanced Imaging API)?

import os
import shutil
from operator import itemgetter
from PIL import Image, ImageDraw, ImageFont

alphabet = 'abcdefghijklmnopqrstuvwxyz'
alphabet += ''.join(map(str.upper, alphabet))
alphabet += '1234567890!@#$%^&*()-_=+[{]}\\|;:\'",<.>/?'

truetype = ImageFont.truetype('C:\\Windows\\Fonts\\Arial.ttf', 48)


def draw_letter(letter):
    img = Image.new('RGB', (100, 100), 'white')
    draw = ImageDraw.Draw(img)
    draw.text((0, 0), letter, font=truetype, fill='#000000')
    # img.save("python-images/{}.png".format(letter), 'PNG')
    return img

def count_black_pixels(img):
    pixels = list(img.getdata())
    return len(list(filter(lambda rgb: sum(rgb) == 0, pixels)))

if __name__ == '__main__':
    # if os.path.exists("python-images"):
    #     shutil.rmtree("python-images")
    # os.makedirs("python-images")
    counts = [
        (letter, count_black_pixels(draw_letter(letter)))
        for letter in alphabet
    ]
    print(sorted(counts, key=itemgetter(1), reverse=True))
