package com.aaron.jboxprueba;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Pelota { // o personaje o enemigo
    BodyDef bodyDef;          // Definición del cuerpo
    Body body;                // Cuerpo del objeto a dibujar
    RectF hitbox;             // Rectangulo con el hitbox.
    World world;              // Mundo JBox2D
    Paint color, pHitbox;     // Color a dibujar la pelota
    Bitmap bitmapPelota;
    float radio;
    boolean visible = true;    // Indica se el personaje se dibuja o no. No afecta a si el personaje existe (los elementos rebotan en el) o no
    boolean activo = true;     // Indica si el personaje, se tiene en cuenta en el mundo Jbox2D. No afecta su visibilidad
    boolean muevoManual=false; // La pelota se mueve manual o de forma automatica
    int oldColor=0;

    Vec2 fuerza;
    Vec2 punto;
    public Pelota(World world, int x, int y, int radio, float density, float friction, float restitution, Bitmap bitmapPelota) {
        this.world = world;
        this.radio = radio;
        this.bitmapPelota = bitmapPelota;

        this.hitbox = new RectF(x - radio, y - radio, x + radio, y + radio);

        color = Utils.paint(Color.CYAN);
//        pHitbox = Utils.paint(Color.CYAN, Paint.Style.FILL, 2);

//        PolygonShape cs = new PolygonShape();

        // Definimos la forma del cuerpo
//        cs.setAsBox(radio,radio);
        // Definimos la forma del cuerpo
        CircleShape cs = new CircleShape();                    // Definimos la forma del cuerpo
        cs.m_radius = radio / PruebaSurfaceView2.divisor;      // Definimos su radio

        FixtureDef fd2 = new FixtureDef();   // Propiedades
        fd2.shape = cs;                      // forma
        fd2.density = density;               // Densidad
        fd2.friction = friction;             // Friccion
        fd2.restitution = restitution;       // Capacidad de Rebote

        this.bodyDef = new BodyDef();        // Se crea la definición del cuerpo
        bodyDef.position.set(x / PruebaSurfaceView2.divisor, y / PruebaSurfaceView2.divisor); // Posición
        bodyDef.type = BodyType.DYNAMIC;     // tipo de cuerpo

        body = world.createBody(bodyDef);     // Se añade al munto
        body.createFixture(fd2);              // Se le añaden las propiedades

        punto = body.getWorldPoint(body.getWorldCenter());     // Punto donde es aplica el impulso, en este caso el centro del cuerpo
    }

    public void dibuja(Canvas c) {
        if (isVisible()){
            c.drawCircle(getX(), getY(), radio, color);
         //  c.drawBitmap(bitmapPelota,getX()-radio/2,getY()-radio/2,null);
//            c.drawRect(hitbox, pHitbox);
        }
    }

    public void aplicaFueza(float fuerzaX, float fuerzaY) {
        this.fuerza = new Vec2(fuerzaX, fuerzaY);                   // Impulso que se aplica en los dos ejes
        body.setLinearVelocity(new Vec2(fuerzaX,fuerzaY));                             // aplicacamos el impulso en ese punto
    }

    public void aplicaFueza() {
        if (this.fuerza != null) {
            body.applyForce(fuerza, punto);                         // aplicacamos el impulso en ese punto
        }
    }

    public void aplicaAceLineal() {
        if (fuerza != null) {
            body.applyLinearImpulse(fuerza, punto);                 // aplicacamos el impulso en ese punto
        }
    }


    public void aplicaAceLineal(float fuerzaX, float fuerzaY) {
        this.fuerza = new Vec2(fuerzaX, fuerzaY);                   // Impulso que se aplica en los dos ejes
        body.applyLinearImpulse(fuerza, punto);                     // aplicacamos el impulso en ese punto
    }

    public void actualizaFisica() {

    }

    public void setPosition(float x, float y) {
        body.setTransform(new Vec2(x / PruebaSurfaceView2.divisor, y / PruebaSurfaceView2.divisor), body.getAngle());
        body.setActive(true);
        body.setAwake(true);
    }

    public void setColor(int color) {
        this.color.setColor(color);
    }

    public Vec2 getPosicion() {
        return body.getPosition();
    }

    public float getX() {
        return body.getPosition().x * PruebaSurfaceView2.divisor;
    }

    public float getY() {
        return body.getPosition().y * PruebaSurfaceView2.divisor;
    }

    public float getXMundo() {
        return body.getPosition().x;
    }

    public float getYMundo() {
        return body.getPosition().y;
    }

    public float getAngle() {
        return body.getAngle();
    }

    public RectF actulizoHitBox() { // actulizo el hitbox por si se mueve
        Vec2 p = body.getPosition();
       // hitbox.set(getX() - radio, getY() - radio, getX() + radio, getY() + radio);

        hitbox.set(getX()-radio, getY()+radio , getX() + radio, getY() + radio);
        return hitbox;
    }

    public boolean isMuevoManual() {
        return muevoManual;
    }

    public void setMuevoManual(boolean muevoManual) {
        this.muevoManual = muevoManual;
        if (this.isMuevoManual()){
            this.oldColor=color.getColor();
            color.setColor(Color.GREEN);
        }
        else color.setColor(oldColor);
    }

    public float getRadio() {
        return radio;
    }

    public float getRadioMundo() {
        return body.getFixtureList().m_shape.m_radius;
    }

    public RectF getHitbox() {
        return hitbox;
    }


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        body.setActive(activo);
    }

    public void destroy(){
        world.destroyBody(body);
    }
}
