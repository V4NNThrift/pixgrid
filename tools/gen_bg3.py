from PIL import Image, ImageDraw, ImageFilter
import os, random, math

os.makedirs('backgrounds', exist_ok=True)
W = H = 1600

def save(img, name, q=92):
    p = f'backgrounds/{name}.webp'
    img.save(p, 'WEBP', quality=q, method=6)
    print(f"  {name}.webp ({os.path.getsize(p)//1024} KB)")

def blob(im, cx, cy, rad, color, alpha):
    layer = Image.new('RGBA', im.size, (0,0,0,0))
    ImageDraw.Draw(layer).ellipse([cx-rad, cy-rad, cx+rad, cy+rad], fill=color+(alpha,))
    layer = layer.filter(ImageFilter.GaussianBlur(max(2, rad//2)))
    im.paste(layer, (0,0), layer)

# Gradient dua warna lembut
def duo(c1, c2, name, angle=45):
    img = Image.new('RGB', (W,H), c1)
    d = ImageDraw.Draw(img)
    diag = int((W+H) * 0.5)
    for i in range(diag):
        t = i / diag
        col = tuple(int(c1[k] + (c2[k]-c1[k])*t) for k in range(3))
        if angle == 45:
            d.line([(i,0),(0,i)], fill=col, width=2)
        else:
            d.line([(0,i),(W,i)], fill=col)
    save(img, name)

duo((20,12,30), (255,140,90), 'duo_ember')
duo((8,16,30), (90,200,230), 'duo_teal')
duo((24,10,24), (200,110,220), 'duo_plum')
duo((10,22,16), (140,240,180), 'duo_sage')
duo((30,24,10), (255,210,120), 'duo_sand')
duo((16,16,22), (200,200,220), 'duo_silver')

# Radial glow
def glow(center, edge, name):
    img = Image.new('RGB', (W,H), edge)
    d = ImageDraw.Draw(img)
    steps = 220
    for i in range(steps, 0, -1):
        t = i / steps
        r = int(max(W,H) * 0.75 * t)
        col = tuple(int(center[k] + (edge[k]-center[k])*t) for k in range(3))
        d.ellipse([W//2-r, H//2-r, W//2+r, H//2+r], fill=col)
    save(img, name)

glow((255,180,120), (26,12,10), 'glow_warm')
glow((130,190,255), (8,12,26), 'glow_cool')
glow((255,150,200), (24,8,18), 'glow_pink')

# Noise warna halus
def neon_noise(c1, c2, name, seed):
    img = Image.new('RGB', (W,H))
    px = img.load()
    random.seed(seed)
    for y in range(H):
        ft = y / H
        base = tuple(int(c1[k] + (c2[k]-c1[k])*ft) for k in range(3))
        for x in range(W):
            n = random.randint(-18, 18)
            px[x,y] = (max(0,min(255,base[0]+n)), max(0,min(255,base[1]+n)), max(0,min(255,base[2]+n)))
    save(img, name, q=86)

neon_noise((40,10,50), (10,30,60), 'noise_plum', 41)
neon_noise((10,30,40), (30,10,50), 'noise_deep', 42)

# Terrain / abstract shapes
def shapes(name, base, palette, seed):
    img = Image.new('RGB', (W,H), base)
    d = ImageDraw.Draw(img)
    random.seed(seed)
    for _ in range(26):
        col = random.choice(palette)
        n = random.randint(3, 6)
        cx, cy = random.randint(-100, W+100), random.randint(-100, H+100)
        rad = random.randint(120, 420)
        pts = []
        for i in range(n):
            ang = 2*math.pi*i/n + random.uniform(-0.3, 0.3)
            rr = rad * random.uniform(0.6, 1.2)
            pts.append((cx + rr*math.cos(ang), cy + rr*math.sin(ang)))
        d.polygon(pts, fill=col)
    img = img.filter(ImageFilter.GaussianBlur(3))
    save(img, name)

shapes('abstract_ember', (16,10,14), [(255,120,70),(200,60,90),(120,40,80)], 51)
shapes('abstract_ocean', (8,14,26), [(50,120,200),(30,80,150),(90,180,210)], 52)
shapes('abstract_lime', (12,20,12), [(90,200,110),(50,150,90),(170,230,120)], 53)

print("\ndone")
