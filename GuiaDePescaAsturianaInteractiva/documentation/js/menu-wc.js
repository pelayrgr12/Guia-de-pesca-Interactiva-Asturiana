'use strict';

customElements.define('compodoc-menu', class extends HTMLElement {
    constructor() {
        super();
        this.isNormalMode = this.getAttribute('mode') === 'normal';
    }

    connectedCallback() {
        this.render(this.isNormalMode);
    }

    render(isNormalMode) {
        let tp = lithtml.html(`
        <nav>
            <ul class="list">
                <li class="title">
                    <a href="index.html" data-type="index-link">GuiaDePescaAsturianaInteractiva documentation</a>
                </li>

                <li class="divider"></li>
                ${ isNormalMode ? `<div id="book-search-input" role="search"><input type="text" placeholder="Type to search"></div>` : '' }
                <li class="chapter">
                    <a data-type="chapter-link" href="index.html"><span class="icon ion-ios-home"></span>Getting started</a>
                    <ul class="links">
                        <li class="link">
                            <a href="index.html" data-type="chapter-link">
                                <span class="icon ion-ios-keypad"></span>Overview
                            </a>
                        </li>
                                <li class="link">
                                    <a href="dependencies.html" data-type="chapter-link">
                                        <span class="icon ion-ios-list"></span>Dependencies
                                    </a>
                                </li>
                                <li class="link">
                                    <a href="properties.html" data-type="chapter-link">
                                        <span class="icon ion-ios-apps"></span>Properties
                                    </a>
                                </li>
                    </ul>
                </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#components-links"' :
                            'data-bs-target="#xs-components-links"' }>
                            <span class="icon ion-md-cog"></span>
                            <span>Components</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="components-links"' : 'id="xs-components-links"' }>
                            <li class="link">
                                <a href="components/AppComponent.html" data-type="entity-link" >AppComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GestionPage.html" data-type="entity-link" >GestionPage</a>
                            </li>
                            <li class="link">
                                <a href="components/HistorialPage.html" data-type="entity-link" >HistorialPage</a>
                            </li>
                            <li class="link">
                                <a href="components/InicioPage.html" data-type="entity-link" >InicioPage</a>
                            </li>
                            <li class="link">
                                <a href="components/LoginPage.html" data-type="entity-link" >LoginPage</a>
                            </li>
                            <li class="link">
                                <a href="components/MapaPage.html" data-type="entity-link" >MapaPage</a>
                            </li>
                            <li class="link">
                                <a href="components/MenuPage.html" data-type="entity-link" >MenuPage</a>
                            </li>
                            <li class="link">
                                <a href="components/MisPuntosPage.html" data-type="entity-link" >MisPuntosPage</a>
                            </li>
                            <li class="link">
                                <a href="components/PerfilPage.html" data-type="entity-link" >PerfilPage</a>
                            </li>
                            <li class="link">
                                <a href="components/RegistroPage.html" data-type="entity-link" >RegistroPage</a>
                            </li>
                            <li class="link">
                                <a href="components/TablaMedidasPage.html" data-type="entity-link" >TablaMedidasPage</a>
                            </li>
                            <li class="link">
                                <a href="components/TabsPage.html" data-type="entity-link" >TabsPage</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#injectables-links"' :
                                'data-bs-target="#xs-injectables-links"' }>
                                <span class="icon ion-md-arrow-round-down"></span>
                                <span>Injectables</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse " ${ isNormalMode ? 'id="injectables-links"' : 'id="xs-injectables-links"' }>
                                <li class="link">
                                    <a href="injectables/HistorialserviceService.html" data-type="entity-link" >HistorialserviceService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/LoginService.html" data-type="entity-link" >LoginService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/MedidaService.html" data-type="entity-link" >MedidaService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/OpenWeatherService.html" data-type="entity-link" >OpenWeatherService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/PerfilService.html" data-type="entity-link" >PerfilService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/PuntoService.html" data-type="entity-link" >PuntoService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/RegistroService.html" data-type="entity-link" >RegistroService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/UsuariosService.html" data-type="entity-link" >UsuariosService</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#guards-links"' :
                            'data-bs-target="#xs-guards-links"' }>
                            <span class="icon ion-ios-lock"></span>
                            <span>Guards</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="guards-links"' : 'id="xs-guards-links"' }>
                            <li class="link">
                                <a href="guards/AuthGuard.html" data-type="entity-link" >AuthGuard</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#interfaces-links"' :
                            'data-bs-target="#xs-interfaces-links"' }>
                            <span class="icon ion-md-information-circle-outline"></span>
                            <span>Interfaces</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? ' id="interfaces-links"' : 'id="xs-interfaces-links"' }>
                            <li class="link">
                                <a href="interfaces/Historial.html" data-type="entity-link" >Historial</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ImagenHistorial.html" data-type="entity-link" >ImagenHistorial</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Medida.html" data-type="entity-link" >Medida</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PuntoMapa.html" data-type="entity-link" >PuntoMapa</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/TipoAnimal.html" data-type="entity-link" >TipoAnimal</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Usuario.html" data-type="entity-link" >Usuario</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Usuarioform.html" data-type="entity-link" >Usuarioform</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/UsuarioLogin.html" data-type="entity-link" >UsuarioLogin</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/UsuarioRegistro.html" data-type="entity-link" >UsuarioRegistro</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#miscellaneous-links"'
                            : 'data-bs-target="#xs-miscellaneous-links"' }>
                            <span class="icon ion-ios-cube"></span>
                            <span>Miscellaneous</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="miscellaneous-links"' : 'id="xs-miscellaneous-links"' }>
                            <li class="link">
                                <a href="miscellaneous/variables.html" data-type="entity-link">Variables</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <a data-type="chapter-link" href="coverage.html"><span class="icon ion-ios-stats"></span>Documentation coverage</a>
                    </li>
                    <li class="divider"></li>
                    <li class="copyright">
                        Documentation generated using <a href="https://compodoc.app/" target="_blank" rel="noopener noreferrer">
                            <img data-src="images/compodoc-vectorise.png" class="img-responsive" data-type="compodoc-logo">
                        </a>
                    </li>
            </ul>
        </nav>
        `);
        this.innerHTML = tp.strings;
    }
});