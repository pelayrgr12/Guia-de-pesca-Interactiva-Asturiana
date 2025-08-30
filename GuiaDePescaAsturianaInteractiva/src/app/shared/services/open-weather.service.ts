import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OpenWeatherService {

  private http = inject(HttpClient);

  getWeather(ciudad: string): Observable<any> {
    const url = `${environment.api}/clima?ciudad=${encodeURIComponent(ciudad)}`;
    return this.http.get(url);
  }
  
  getIcono(icon: string) {
    const url = `https://openweathermap.org/img/wn/${icon}.png`
    return url;
  }


  

}
