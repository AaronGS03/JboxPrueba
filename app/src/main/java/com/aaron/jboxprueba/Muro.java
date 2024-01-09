package com.aaron.jboxprueba;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Muro { // o plataforma
    int codigo;               // Codigo identificativo de la plataforma
    BodyDef bodyDef;          // Definición del cuerpo
    Body body;                // Cuerpo del objeto a dibujar
    RectF hitbox;             // Rectangulo con el hitbox.
    World world;              // Mundo JBox2D
    Paint color;              // Color a dibujar el muro
    boolean visible=true;     // Indica se el muro se dibuja o no. No afecta a si el muro existe (los elementos rebotan en el) o no
    boolean activo=true;      // Indica si el muro, se tiene en cuenta en el mundo Jbox2D. No afecta su visibilidad


    public Muro(int codigo,World world, RectF hitbox, float density, float friction, float restitution) {
        this.hitbox = hitbox;
        this.world=world;
        this.codigo=codigo;

        color=Utils.paint(Color.BLACK);
        // Vec2[] vv = ps.getVertices();

        PolygonShape ps = new PolygonShape(); // Definimos la forma del cuerpo

        // los vertices estan centrados
        ps.setAsBox(((hitbox.right - hitbox.left) / 2) / PruebaSurfaceView2.divisor, ((hitbox.bottom - hitbox.top) / 2) / PruebaSurfaceView2.divisor);

        FixtureDef fd2 = new FixtureDef();  // Propiedades
        fd2.shape = ps;                     // forma
        fd2.density = density;              // Densidad
        fd2.friction = friction;            // Friccion
        fd2.restitution = restitution;      // Capacidad de Rebote

        this.bodyDef = new BodyDef();       // Se crea la definición del cuerpo
        // Posición
        bodyDef.position.set(hitbox.centerX() / PruebaSurfaceView2.divisor, hitbox.centerY() / PruebaSurfaceView2.divisor);
        bodyDef.type = BodyType.STATIC;     // tipo de cuerpo

        body = world.createBody(bodyDef);  // Se añade al munto
        body.createFixture(fd2);           // Se le añaden las propiedades
   }

    public void dibuja(Canvas c) {
        if (visible) c.drawRect(hitbox, color);
    }

    public void destroy(){
        world.destroyBody(body);
    }

    public void setPosition(float x, float y) {
        body.setTransform(new Vec2(x / PruebaSurfaceView2.divisor, y / PruebaSurfaceView2.divisor), body.getAngle());
        body.setActive(true);
        body.setAwake(true);
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

    public RectF getHitBox() {
        return hitbox;
    }

    public int getCodigo() {
        return codigo;
    }
}
