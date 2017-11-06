# Javanaise
	Projet Javanaise classe M2GI 2017/2018
	TOULISSE Albin
	
	
# Fonctionnalités
	-Projet fonctionnant via l'utilisation d'un proxy
		Permettant de cacher l'utilisation des verrous à l'utilisateur
		Lancement via La classe Irc2
		Utiliser Irc pour la version 1 sans proxy
		
	-Ajout de deux classes Rush et Rush2
		Rush effectue aléatoirement des read et write en boucle
		Rush2 effectue un read une fois toutes les 2 secondes afin de surveiller l'état
		
		
# Extensions
	Gestion de panne client:
		En cas de panne client, le coordinateur va terminate le client ne répondant plus
		Il renvoit ensuite sa propre donnée lorsqu'un autre client demande celle du client ne répondant plus
	Ajout d'un bouton flush:
		Un bouton Flush a été rajouté aux boutons Read et Write
		Ce bouton élimine l'objet de la liste en mémoire du client
		Cet objet est réinséré lorsque l'utilisateur effectue un read ou un write