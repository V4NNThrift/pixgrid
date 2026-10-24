from PIL import Image, ImageDraw, ImageFilter
import os, random, math

os.makedirs('backgrounds', exist_ok=True)
os.makedirs('textures', exist_ok=True)
W = H = 1600

def save(img, name, folder='backgrounds', q=92):
    p = f'{folder}/{name}.webp'
    img.save(p, 'WEBP', quality=q, method=6)
    print(f"  {name}.webp ({os.path.getsize(p)//1024} KB)")

def blob(im, cx, cy, rad, color, alpha):
    layer = Image.new('RGBA', im.size, (0,0,0,0))
    ImageDraw.Draw(layer).ellipse([cx-rad, cy-rad, cx+rad, cy+rad], fill=color+(alpha,))
    layer = layer.filter(ImageFilter.GaussianBlur(max(2, rad//2)))
    im.paste(layer, (0,0), layer)

# ---------- Tekstur bercorak yang benar-benar dipakai ----------
# Kayu gelap
img = Image.new('RGB', (W,H), (72,48,32))
px = img.load()
random.seed(201)
for y in range(H):
    n = random.randint(-6,6) + int(7*math.sin(y/24))
    base = (72+n, 48+n, 32+n)
    for x in range(W):
        px[x,y] = base
d = ImageDraw.Draw(img)
for _ in range(360):
    x = random.randint(0,W)
    pts = [(x+int(11*math.sin(y/34)), y) for y in range(0,H,10)]
    d.line(pts, fill=(52,32,20), width=random.randint(1,4))
save(img, 'wood_dark', 'textures')

# Semen / mortar
img = Image.new('RGB', (W,H), (166,164,158))
px = img.load()
random.seed(202)
for y in range(H):
    for x in range(W):
        n = random.randint(-24,24)
        r,g,b = px[x,y]
        px[x,y] = (max(0,min(255,r+n)), max(0,min(255,g+n)), max(0,min(255,b+n-2)))
save(img, 'mortar', 'textures', q=86)

# Kain tenun
img = Image.new('RGB', (W,H), (238,230,218))
d = ImageDraw.Draw(img)
random.seed(203)
for y in range(0, H, 6):
    d.line([(0,y),(W,y)], fill=(224,214,200), width=3)
for x in range(0, W, 6):
    d.line([(x,0),(x,H)], fill=(230,222,208), width=3)
save(img, 'weave', 'textures')

# Emboss garis diagonal
img = Image.new('RGB', (W,H), (40,40,48))
d = ImageDraw.Draw(img)
for i in range(-H, W+H, 24):
    d.line([(i,0),(i+H,H)], fill=(56,56,68), width=10)
    d.line([(i+10,0),(i+10+H,H)], fill=(28,28,34), width=10)
save(img, 'emboss', 'textures')

# Kaca retak
img = Image.new('RGB', (W,H), (200,220,235))
d = ImageDraw.Draw(img)
random.seed(204)
cx, cy = W//2, H//2
for _ in range(52):
    ang = random.uniform(0, 2*math.pi)
    L = random.randint(150, 900)
    pts = [(cx, cy)]
    x, y = cx, cy
    steps = random.randint(2, 5)
    for s in range(steps):
        x += int(L/steps * math.cos(ang + random.uniform(-0.4,0.4)))
        y += int(L/steps * math.sin(ang + random.uniform(-0.4,0.4)))
        pts.append((x,y))
    d.line(pts, fill=(240,248,255), width=random.randint(1,4))
save(img, 'cracked', 'textures')

# Lumut
img = Image.new('RGB', (W,H), (46,64,38))
random.seed(205)
for _ in range(180):
    blob(img, random.randint(0,W), random.randint(0,H), random.randint(60,300),
         random.choice([(70,110,50),(50,90,42),(90,130,60)]), random.randint(50,140))
save(img, 'moss', 'textures')

# ---------- Background bernuansa musim / suasana ----------
def season(base, palette, name, seed, n=40):
    img = Image.new('RGB', (W,H), base)
    random.seed(seed)
    for _ in range(n):
        blob(img, random.randint(0,W), random.randint(0,H), random.randint(200,600),
             random.choice(palette), random.randint(45,130))
    save(img, name)

season((16,20,14), [(120,180,80),(80,140,60),(160,200,110)], 'season_spring', 301)
season((30,20,10), [(220,150,60),(200,110,40),(255,200,110)], 'season_autumn', 302)
season((10,16,30), [(90,140,220),(60,100,180),(140,190,240)], 'season_winter', 303)
season((26,8,10), [(220,80,60),(180,50,40),(255,140,100)], 'season_summer', 304)

# Stripes warna tebal
def stripes(name, cols, seed, width=52):
    img = Image.new('RGB', (W,H), cols[0])
    d = ImageDraw.Draw(img)
    random.seed(seed)
    i = 0
    x = -H
    while x < W + H:
        col = cols[i % len(cols)]
        d.line([(x,0),(x+H,H)], fill=col, width=width)
        x += width
        i += 1
    save(img, name)

stripes('stripes_warm', [(255,140,90),(255,190,120),(200,80,70)], 401)
stripes('stripes_cool', [(90,160,230),(140,200,250),(50,90,160)], 402)
stripes('stripes_mono', [(30,30,36),(60,60,70),(18,18,22)], 403)

# Gelombang laut
img = Image.new('RGB', (W,H), (16,50,80))
d = ImageDraw.Draw(img)
random.seed(404)
for i in range(70):
    y0 = random.randint(0,H)
    amp = random.randint(6,26)
    freq = random.uniform(0.006,0.02)
    col = random.choice([(30,90,140),(20,70,120),(50,120,170),(10,50,90)])
    pts = [(x, y0 + int(amp*math.sin(x*freq + i))) for x in range(0,W+1,10)]
    d.line(pts, fill=col, width=random.randint(3,12))
save(img, 'waves_ocean')

# Konfeti
img = Image.new('RGB', (W,H), (250,248,252))
d = ImageDraw.Draw(img)
random.seed(405)
cols = [(255,120,90),(120,180,250),(255,200,90),(140,220,150),(200,140,240)]
for _ in range(500):
    x,y = random.randint(0,W), random.randint(0,H)
    w,h = random.randint(10,26), random.randint(4,10)
    d.rectangle([x,y,x+w,y+h], fill=random.choice(cols))
save(img, 'confetti_party')

# Bintik halus (speckle)
img = Image.new('RGB', (W,H), (240,238,236))
d = ImageDraw.Draw(img)
random.seed(406)
for _ in range(9000):
    x,y = random.randint(0,W), random.randint(0,H)
    r = random.choice([1,1,1,2])
    v = random.randint(180,225)
    d.ellipse([x-r,y-r,x+r,y+r], fill=(v,v,v))
save(img, 'speckle')

print("\ndone")
