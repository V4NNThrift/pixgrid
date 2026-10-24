from PIL import Image, ImageDraw, ImageFilter
import os, random, math

os.makedirs('textures', exist_ok=True)
os.makedirs('backgrounds', exist_ok=True)

W = H = 1800

def save(img, path, q=90):
    img.save(path, 'WEBP', quality=q, method=6)
    print(f"  {os.path.basename(path)} ({os.path.getsize(path)//1024} KB)")

def blob(im, cx, cy, rad, color, alpha):
    layer = Image.new('RGBA', im.size, (0,0,0,0))
    ImageDraw.Draw(layer).ellipse([cx-rad, cy-rad, cx+rad, cy+rad], fill=color+(alpha,))
    layer = layer.filter(ImageFilter.GaussianBlur(max(2, rad//2)))
    im.paste(layer, (0,0), layer)

# ---------- TEKSTUR RESOLUSI TINGGI ----------

# Kain kanvas
img = Image.new('RGB', (W,H), (222,214,198))
d = ImageDraw.Draw(img)
random.seed(1)
for y in range(0, H, 2):
    v = random.randint(-6, 6)
    d.line([(0,y),(W,y)], fill=(222+v,214+v,198+v))
for x in range(0, W, 2):
    v = random.randint(-5, 5)
    d.line([(x,0),(x,H)], fill=(218+v,210+v,194+v))
save(img, 'textures/canvas.webp')

# Kulit
img = Image.new('RGB', (W,H), (150,105,70))
px = img.load()
random.seed(2)
for y in range(H):
    for x in range(W):
        n = random.randint(-14,14) + int(5*math.sin(x/13) + 5*math.sin(y/29))
        r,g,b = px[x,y]
        px[x,y] = (max(0,min(255,r+n)), max(0,min(255,g+n)), max(0,min(255,b+n)))
save(img, 'textures/leather.webp', q=88)

# Tekstur batu
img = Image.new('RGB', (W,H), (128,126,120))
d = ImageDraw.Draw(img)
random.seed(3)
for _ in range(420):
    cx, cy = random.randint(0,W), random.randint(0,H)
    rad = random.randint(30, 160)
    shade = random.randint(105, 150)
    blob(img, cx, cy, rad, (shade, shade-2, shade-8), random.randint(60,130))
img = img.filter(ImageFilter.GaussianBlur(1))
save(img, 'textures/stone.webp')

# Pasir
img = Image.new('RGB', (W,H), (214,192,156))
px = img.load()
random.seed(4)
for y in range(H):
    for x in range(W):
        n = random.randint(-18,18)
        r,g,b = px[x,y]
        px[x,y] = (max(0,min(255,r+n)), max(0,min(255,g+n-2)), max(0,min(255,b+n-6)))
save(img, 'textures/sand.webp', q=86)

# Bata
img = Image.new('RGB', (W,H), (150,84,68))
d = ImageDraw.Draw(img)
bh, bw = 64, 140
random.seed(5)
for row in range(0, H//bh + 1):
    offset = (bw//2) if row % 2 else 0
    for col in range(-1, W//bw + 1):
        x0 = col*bw + offset
        y0 = row*bh
        shade = random.randint(-16, 16)
        d.rectangle([x0, y0, x0+bw-6, y0+bh-6], fill=(150+shade, 84+shade//2, 68+shade//2))
save(img, 'textures/brick.webp')

# Beton kasar
img = Image.new('RGB', (W,H), (142,142,148))
px = img.load()
random.seed(6)
for y in range(H):
    for x in range(W):
        n = random.randint(-22,22)
        r,g,b = px[x,y]
        px[x,y] = (max(0,min(255,r+n)), max(0,min(255,g+n)), max(0,min(255,b+n)))
save(img, 'textures/concrete_hi.webp', q=86)

# Air
img = Image.new('RGB', (W,H), (30,90,140))
d = ImageDraw.Draw(img)
random.seed(7)
for i in range(220):
    y = random.randint(0, H)
    amp = random.randint(3, 12)
    freq = random.uniform(0.008, 0.03)
    pts = [(x, y + int(amp*math.sin(x*freq + i))) for x in range(0, W+1, 8)]
    shade = random.randint(-20, 30)
    d.line(pts, fill=(30+shade, 90+shade, 140+shade), width=random.randint(2,7))
save(img, 'textures/water.webp')

# Awan / langit
img = Image.new('RGB', (W,H), (120,170,220))
random.seed(8)
for _ in range(60):
    blob(img, random.randint(0,W), random.randint(0,H), random.randint(200,550),
         (245,248,255), random.randint(40,110))
save(img, 'textures/sky.webp')

# Daun tropis
img = Image.new('RGB', (W,H), (14,40,24))
d = ImageDraw.Draw(img)
random.seed(9)
for _ in range(70):
    cx, cy = random.randint(0,W), random.randint(0,H)
    ang = random.uniform(0, 2*math.pi)
    L = random.randint(90, 260)
    wdt = random.randint(22, 52)
    p1 = (cx + L*math.cos(ang), cy + L*math.sin(ang))
    p2 = (cx + wdt*math.cos(ang+1.57), cy + wdt*math.sin(ang+1.57))
    p3 = (cx + wdt*math.cos(ang-1.57), cy + wdt*math.sin(ang-1.57))
    shade = random.randint(-18, 24)
    d.polygon([ (cx,cy), p2, p1, p3 ], fill=(30+shade, 90+shade, 46+shade))
save(img, 'textures/leafy.webp')

# Bunga (bokeh organik)
img = Image.new('RGB', (W,H), (28,16,34))
random.seed(10)
for _ in range(110):
    cx, cy = random.randint(0,W), random.randint(0,H)
    rad = random.randint(40, 190)
    col = random.choice([(255,150,200),(230,120,190),(255,190,220),(200,110,220)])
    blob(img, cx, cy, rad, col, random.randint(35,110))
save(img, 'textures/bloom.webp')

# ---------- BACKGROUND TAMBAHAN ----------

# Aurora
img = Image.new('RGB', (W,H), (6,10,22))
random.seed(11)
for i in range(14):
    pts = []
    base_y = random.randint(100, H-100)
    for x in range(0, W+1, 40):
        y = base_y + int(180*math.sin(x/420 + i*0.7))
        pts.append((x,y))
    layer = Image.new('RGBA', (W,H), (0,0,0,0))
    ld = ImageDraw.Draw(layer)
    ld.line(pts, fill=random.choice([(80,255,190),(90,180,255),(150,255,220),(120,120,255)]) + (70,), width=random.randint(40,120))
    layer = layer.filter(ImageFilter.GaussianBlur(40))
    img.paste(layer, (0,0), layer)
save(img, 'backgrounds/aurora.webp')

# Galaxy
img = Image.new('RGB', (W,H), (6,4,16))
random.seed(12)
for _ in range(34):
    blob(img, random.randint(0,W), random.randint(0,H), random.randint(200,620),
         random.choice([(120,80,220),(60,110,210),(190,80,190),(70,200,220)]),
         random.randint(28,80))
# bintang
d = ImageDraw.Draw(img)
for _ in range(1400):
    x,y = random.randint(0,W), random.randint(0,H)
    r = random.choice([1,1,1,2])
    v = random.randint(150,255)
    d.ellipse([x-r,y-r,x+r,y+r], fill=(v,v,v))
save(img, 'backgrounds/galaxy.webp')

# Api
img = Image.new('RGB', (W,H), (26,6,4))
random.seed(13)
for _ in range(46):
    blob(img, random.randint(0,W), random.randint(int(H*0.4),H), random.randint(140,480),
         random.choice([(255,90,20),(255,160,40),(255,220,110),(200,40,10)]),
         random.randint(40,120))
save(img, 'backgrounds/fire.webp')

# Es
img = Image.new('RGB', (W,H), (12,26,42))
random.seed(14)
for _ in range(40):
    blob(img, random.randint(0,W), random.randint(0,H), random.randint(180,520),
         random.choice([(150,220,255),(90,170,240),(210,240,255)]),
         random.randint(30,90))
save(img, 'backgrounds/ice.webp')

# Terpal / tie dye
img = Image.new('RGB', (W,H), (240,236,246))
random.seed(15)
for _ in range(28):
    blob(img, random.randint(0,W), random.randint(0,H), random.randint(260,620),
         random.choice([(255,160,200),(170,200,255),(255,230,160),(180,255,210)]),
         random.randint(90,170))
save(img, 'backgrounds/tiedye.webp')

# Neon grid (retro)
img = Image.new('RGB', (W,H), (10,4,24))
d = ImageDraw.Draw(img)
for x in range(0, W, 90):
    d.line([(x,0),(x,H)], fill=(120,40,180), width=2)
for y in range(0, H, 90):
    d.line([(0,y),(W,y)], fill=(120,40,180), width=2)
blob(img, int(W*0.5), int(H*0.75), 700, (255,60,160), 90)
blob(img, int(W*0.5), int(H*0.2), 500, (60,180,255), 70)
save(img, 'backgrounds/neon_grid.webp')

# Halftone
img = Image.new('RGB', (W,H), (20,20,28))
d = ImageDraw.Draw(img)
for y in range(0, H, 18):
    for x in range(0, W, 18):
        r = int(4 + 5*abs(math.sin(x/220)) + 3*abs(math.cos(y/180)))
        d.ellipse([x-r,y-r,x+r,y+r], fill=(230,230,240))
save(img, 'backgrounds/halftone.webp')

# Garis miring warna
img = Image.new('RGB', (W,H), (250,248,252))
d = ImageDraw.Draw(img)
random.seed(16)
for i in range(-H, W+H, 46):
    d.line([(i,0),(i+H,H)], fill=random.choice([(255,190,210),(190,210,255),(255,235,180)]), width=22)
save(img, 'backgrounds/diagonal_pop.webp')

print("\ndone")
