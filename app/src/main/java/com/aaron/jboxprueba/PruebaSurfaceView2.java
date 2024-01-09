package com.aaron.jboxprueba;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.aaron.jboxprueba.enums.Plataformas;
import com.aaron.jboxprueba.enums.Muros;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class PruebaSurfaceView2 extends SurfaceView implements SurfaceHolder.Callback {


    private SurfaceHolder surfaceHolder;     // Interfaz abstracta para manejar la superficie de dibujado
    private Context context;                 // Contexto de la aplicación
    private int anchoPantalla = 1;           // Ancho de la pantalla, su valor se actualiza en el método surfaceChanged
    private int altoPantalla = 1;            // Alto de la pantalla, su valor se actualiza en el método surfaceChanged
    private Bitmap bitmapFondo;
    private Bitmap bitmapPelota;
    private Hilo hilo;                       // Hilo encargado de dibujar y actualizar la física
    private boolean funcionando = false;     // Control del hilo

    static int divisor = 10;                 // Divisor para establecer el tamaño del mundo en Jbox2d: tamañaPantall/divisor
    String cadDedo = "";                     // Cadena aux para mostrar la posición pulsada en pantalla

    int radio = (anchoPantalla / 400);
    boolean inicio = true;
    World world;                             // Mundo JBox2D
    float timeStep = 1.0f / 10.f;            // Tiempo entre cada ejecución
    int velocityIterations = 6;              // Número  de  iteraciones  en  la  fase  de  velocidad.
    int positionIterations = 2;              // Número de iteraciones en la fase de posición

    boolean aplicofuerza = false;

    ArrayList<Muro> muros;                  // Lista de muros
    ArrayList<Muro> plataformas;            // Lista de plataformas
    Bitmap btnAvz, btnRetrocede, btnSalta;

    Paint p1;                               // Paint auxiliar para el texto
    Pelota personaje;

    public Bitmap getBitmapFromAssets(String fichero) {
        try {
            InputStream is = context.getAssets().open(fichero);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PruebaSurfaceView2(Context context) {   // Constructor
        super(context);
        this.surfaceHolder = getHolder();             //  Se obtiene el  holder
        this.surfaceHolder.addCallback(this);         //  y se indica donde van las funciones callback
        this.context = context;                       // Obtenemos el contexto

        hilo = new Hilo();                            // Inicializamos el hilo
        setFocusable(true);                           // Aseguramos que reciba eventos de toque


        Vec2 gravity = new Vec2(0, 9.8f);  // Gravedad del mundo. Se establece una pequeña gravedad de izquierda a derecha
        this.world = new World(gravity);             // Se establece la grabedad
        this.world.setSleepingAllowed(true);         // Se permite que los objetos que esten en reposo no se tengan en cuenta en las iteraciones

        muros = new ArrayList<>();
        plataformas = new ArrayList<>();
        bitmapFondo = getBitmapFromAssets("imagenes/images(1).jpg");

        this.btnAvz = getBitmapFromAssets("imagenes/ad.png");
        this.btnRetrocede = getBitmapFromAssets("imagenes/ai.png");
        this.btnSalta = getBitmapFromAssets("imagenes/Aa.png");
        this.bitmapPelota = getBitmapFromAssets("imagenes/pen.jpg");
    }

    public void definirMundo() {

        p1 = Utils.paint(Color.BLACK, altoPantalla / 20);


        int[] colores = {Color.WHITE, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.GREEN, Color.DKGRAY, Color.LTGRAY, Color.YELLOW, Color.CYAN};
        int cont = 0;
        //pelotaAux.setColor(colores[cont % 9]);
        personaje = new Pelota(world, 2 * anchoPantalla / 12, 3 * altoPantalla / 12, anchoPantalla / 40, 1f, 1f, 0, bitmapPelota);
        muros.add(new Muro(Muros.ARRIBA.getCodigo(), world, new RectF(0, 0, anchoPantalla, altoPantalla / 60), 10f, 1f, 0));
        muros.add(new Muro(Muros.DERECHA.getCodigo(), world, new RectF(anchoPantalla - anchoPantalla / 60, 0, anchoPantalla, altoPantalla), 10f, 1f, 0));
        muros.add(new Muro(Muros.IZQUIERDA.getCodigo(), world, new RectF(0, 0, anchoPantalla / 60, altoPantalla), 10f, 1f, 0));
        muros.add(new Muro(Muros.ABAJO.getCodigo(), world, new RectF(0, altoPantalla - altoPantalla / 6, anchoPantalla, altoPantalla), 10f, 1f, 0));

        plataformas.add(new Muro(Plataformas.ARRIBA.getCodigo(), world, new RectF(anchoPantalla / 10 * 3, altoPantalla / 10 * 2, anchoPantalla / 10 * 3.5f, altoPantalla / 10 * 3), 10f, 0.5f, 0));
        plataformas.add(new Muro(Plataformas.MEDIO.getCodigo(), world, new RectF(anchoPantalla / 10 * 6, altoPantalla / 10 * 3, anchoPantalla / 10 * 6.5f, altoPantalla / 10 * 6), 10f, 0.5f, 0));
        plataformas.add(new Muro(Plataformas.ABAJO.getCodigo(), world, new RectF(anchoPantalla / 10 * 2, altoPantalla / 10 * 5, anchoPantalla / 10 * 2.5f, altoPantalla / 10 * 6.5f), 10f, 0.5f, 0));
        plataformas.add(new Muro(Plataformas.HORIZONTAL.getCodigo(), world, new RectF(anchoPantalla / 10 * 3, altoPantalla / 10 * 7.5f, anchoPantalla / 10 * 6, altoPantalla / 10 * 7.75f), 10f, 0.5f, 0));

        // plataformas.add(new Muro(Plataformas.ABAJO.getCodigo(), world, new RectF(anchoPantalla / 10 * 2, altoPantalla / 10 * 5, anchoPantalla / 10 * 2.5f, altoPantalla / 10 * 6.5f), 10f, 0.5f, 0));

        // plataformas.add(new Muro(Plataformas.HORIZONTAL.getCodigo(), world, new RectF(anchoPantalla / 10 * 0.3f, altoPantalla / 10 * 8.9f, anchoPantalla / 10 * 0.7f, altoPantalla / 10 * 9.5f), 10f, 0.5f, 0));
        // plataformas.get(2).setColor(colores[0]);
    }


    public void actualizarFisica() {           // Actualizamos la física de los elementos en pantalla
        world.step(timeStep, velocityIterations, positionIterations);

        if (!btnIPulsado && !btnDPulsado && !btnSPulsado) {
            personaje.aplicaFueza(0, world.getGravity().y);
        }

        // aplico fuerzas a las pelotas
        if (aplicofuerza) {
            personaje.aplicaAceLineal();  // Muevo con la acceleración establecida en onTouch
            //}

        }

        // para cada pelota activa actulizo su hitbox por si se ha movido

        if (personaje.isActivo()) personaje.actulizoHitBox();


    }


    public void dibujar(Canvas c) {        // Rutina de dibujo en el lienzo. Se le llamará desde el hilo
        try {
            c.drawColor(Color.GRAY);
            c.drawBitmap(bitmapFondo, 0, 0, null);
            c.drawText(anchoPantalla + ":" + altoPantalla, 100, 100, p1); // tamaño de la pantalla
            c.drawText(cadDedo, 100, 200, p1);                                 // lugar de la última pulsación en pantalla
            for (Muro m : muros) m.dibuja(c); // dibujo los muros
            for (Muro p : plataformas) p.dibuja(c); // dibujo los muros
            personaje.dibuja(c);  // dibujo las pelotas
            c.drawBitmap(btnAvz, 280, 935, null);
            c.drawBitmap(btnRetrocede, 80, 935, null);
            c.drawBitmap(btnSalta, 1600, 935, null);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean btnSPulsado = false;
    boolean btnDPulsado = false;
    boolean btnIPulsado = false;
    int fuerzax = 10;
    int fuerzay = -62000;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int accion = event.getAction();                    //  Solo gestiona la pulsación de un dedo.
        int accion2 = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();

        switch (accion2) {
            case MotionEvent.ACTION_DOWN:
                // pelota1.setPosition(anchoPantalla/5*1, altoPantalla/10*1); // muevo una pelota a una posición especifica

                //  aplicofuerza = true;
//                for (Pelota p : pelotas) {
//
//                    if (p.getHitbox().contains(x, y)) {
//                        p.setMuevoManual(!p.isMuevoManual());
//                    }
//

//                }

                if (x > (80) && x < (80 + btnRetrocede.getWidth()) && (y > 935) && (y < 935 + btnRetrocede.getHeight())) {
                    btnIPulsado = true;
                }
                if (x > (280) && x < (280 + btnAvz.getWidth()) && (y > 935) && (y < 935 + btnAvz.getHeight())) {
                    btnDPulsado = true;
                }
                if (x > (1600) && x < (1600 + btnSalta.getWidth()) && (y > 935) && (y < 935 + btnSalta.getHeight())) {
                    btnSPulsado = true;
                }


                if (btnIPulsado) {
                    btnDPulsado = false;
                    fuerzax=-10;
                    personaje.aplicaFueza(fuerzax, 0);


                } else if (btnDPulsado) {
                    btnIPulsado = false;
                    fuerzax=10;
                    personaje.aplicaFueza(fuerzax, 0);


                } else if (btnSPulsado) {
                    for (Muro plataforma :
                            plataformas) {
                        if (personaje.getHitbox().intersect(muros.get(3).hitbox) || personaje.getHitbox().intersect(plataforma.hitbox)) {
                            personaje.aplicaFueza(0, -600);


                        } else {

                            btnSPulsado = false;
                        }
                    }
                }
//                    if (!pelotas.get(i).isMuevoManual()) {
//                        if (x < anchoPantalla / 2) {
//                            //pelotas.get(i).fuerza=new Vec2(40,0);
//                            //pelotas.get(i).aplicaFueza(-300,0);
//                            pelotas.get(i).aplicaAceLineal(-300, -150 * PruebaSurfaceView2.divisor);
//                        }
//                        else {
//                            //pelotas.get(i).fuerza=new Vec2(-40,0);
//                            pelotas.get(i).aplicaFueza(-300,0);
//                            pelotas.get(i).aplicaAceLineal(300, -150 * PruebaSurfaceView2.divisor);
//                        }
//                    } else { // Si la pelota se mueve manual, si se pulsa en la mitad izquierda se mueve hacia arriba y la izquierda, si se pulsa en la parte derecha -> hacia arriba y a la derecha
//                        if (x < anchoPantalla / 2) {
//                            //pelotas.get(i).fuerza=new Vec2(40,0);
//                           // pelotas.get(i).aplicaFueza(40,-40f);
//                            pelotas.get(i).aplicaAceLineal(-300, -150 * PruebaSurfaceView2.divisor);
//                        }
//                        else {
//                            //pelotas.get(i).fuerza=new Vec2(-40,0);
//                            //pelotas.get(i).aplicaFueza(40,-40);
//                            pelotas.get(i).aplicaAceLineal(300, -150 * PruebaSurfaceView2.divisor);
//                        }
//                    }


                cadDedo = "" + x + ":" + y;
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                for (Muro plataforma :
                        plataformas) {
                    if (personaje.getHitbox().intersect(muros.get(3).hitbox) || personaje.getHitbox().intersect(plataforma.hitbox)) {

                                personaje.aplicaAceLineal(fuerzax, -10000);


                    }


                }
                return true;

            case MotionEvent.ACTION_UP:
                btnIPulsado = false;
                btnDPulsado = false;
                if (btnSPulsado) {
                    btnSPulsado = false;
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                if (btnSPulsado) {
                    btnSPulsado = false;
                }
        }

        return false;
    }


    public float mtop(float meters) {
        return meters * PruebaSurfaceView2.divisor;
    }

    public float ptom(float pixels) {
        return pixels / PruebaSurfaceView2.divisor;
    }

    public int mtopi(float meters) {
        return (int) (meters * PruebaSurfaceView2.divisor);
    }

    public int ptomi(float pixels) {
        return (int) (pixels / PruebaSurfaceView2.divisor);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        anchoPantalla = width;
        altoPantalla = height;

        if (inicio) {
            // definirMundo();  // Definimos los elementos del mundo despues de obtener el ancho y el alto del mundo
            inicio = false;
        }

        hilo.setSurfaceSize(width, height);
        hilo.setFuncionando(true);
        if (hilo.getState() == Thread.State.NEW) {
            definirMundo();
            hilo.start();
        } else {
            if (hilo.getState() == Thread.State.TERMINATED) {
                hilo = new Hilo();
                hilo.start();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hilo.setFuncionando(false);
        try {
            hilo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    class Hilo extends Thread {
        public Hilo() {
        }

        @Override
        public void run() {
            while (funcionando) {
                Canvas c = null;    //Siempre es necesario repintar todo el lienzo
                try {
                    if (!surfaceHolder.getSurface().isValid())
                        continue;   // si la superficie no está preparada repetimos

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        c = surfaceHolder.lockHardwareCanvas();   // Obtenemos el lienzo con Aceleración Hardware
                    }
                    if (c == null)
                        c = surfaceHolder.lockCanvas();  // Si no podemos obtener el lienzo con acerelación HW la obtenemos por software
                    synchronized (surfaceHolder) {       // La sincronización es necesaria por ser recurso común
                        actualizarFisica();              // Se mueven los objetos
                        dibujar(c);                      // Se dibujan los elements
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {                              // Haya o no excepción, hay que liberar el lienzo
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        // Activa o desactiva el funcionamiento del hilo
        void setFuncionando(boolean flag) {
            funcionando = flag;
        }

        // Función llamada si cambia el tamaño del view
        public void setSurfaceSize(int width, int height) {
            synchronized (surfaceHolder) {

                bitmapFondo = getBitmapFromAssets("imagenes/images(1).jpg");
                bitmapFondo = Bitmap.createScaledBitmap(
                        bitmapFondo, width, height, true);
            }
            btnRetrocede = Bitmap.createScaledBitmap(btnRetrocede, width / 16, width / 16, true);
            btnAvz = Bitmap.createScaledBitmap(btnAvz, width / 16, width / 16, true);
            btnSalta = Bitmap.createScaledBitmap(btnSalta, width / 16, width / 16, true);
            bitmapPelota = Bitmap.createScaledBitmap(bitmapPelota, width / 16, width / 16, true);
        }
    }
}

