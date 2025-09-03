// JavaScript로 모든 동적 기능을 처리합니다.

// === 지도 및 날씨 관련 기능 ===

// 지도 초기화
var mapContainer = document.getElementById('map'),
    mapOption={
        center:new daum.maps.LatLng(37.537187,127.005476),
        level:5 //지도 확대 레벨
    };
var map =new daum.maps.Map(mapContainer,mapOption);
var geocoder = new daum.maps.services.Geocoder();
var marker = new daum.maps.Marker({
    position:new daum.maps.LatLng(37.537187,127.005476),
    map:map
});

/**
 * 카카오 주소 검색 API를 이용해 주소를 찾고 지도에 표시합니다.
 * Promise를 반환하여 비동기 처리를 용이하게 합니다.
 * @returns {Promise<void>}
 */
function adress() {
    return new Promise((resolve, reject) => {
        new daum.Postcode({
            oncomplete:function (data){
                var addr = data.address;
                document.getElementById("searchInput").value = addr;
                geocoder.addressSearch(data.address, function(results, status) {
                    if (status === daum.maps.services.Status.OK) {
                        var result = results[0];
                        var coords = new daum.maps.LatLng(result.y, result.x);
                        mapContainer.style.display = "block";
                        map.relayout();
                        map.setCenter(coords);
                        marker.setPosition(coords);
                        var nx = coords.getLat();
                        var ny = coords.getLng();
                        document.getElementById("nx").value = nx;
                        document.getElementById("ny").value = ny;
                        var regi=results[0].road_address.region_1depth_name;
                        if(regi ==="전남"||regi==="전북"||regi==="경북"||regi==="경남"||regi==="충남"||regi==="충북"||regi==="강원"||regi==="경기" ) {
                            city= results[0].road_address.region_2depth_name.substring(0,2);
                        }else{
                            city = results[0].road_address.region_1depth_name;
                        }
                        document.getElementById("city").value = city;
                        resolve();
                    } else {
                        reject(new Error('Address search failed'));
                    }
                });
            }
        }).open();
    });
}

/**
 * 주소 검색 후 날씨 정보를 가져오는 비동기 함수
 */
async function combinedFunction() {
    try {
        await adress();
        getWeather();
        getWeek();
    } catch (error) {
        console.error('주소 검색 중 오류 발생:', error);
    }
}

/**
 * 오늘의 날씨 정보를 API에서 가져와 화면에 표시합니다.
 */
function getWeather() {
    var nx = $('#nx').val();
    var ny = $('#ny').val();

    // 기존 목록 비우기
    document.getElementById('tmpList').innerHTML = '';
    document.getElementById('popList').innerHTML = '';
    document.getElementById('ptyList').innerHTML = '';
    document.getElementById('rehList').innerHTML = '';
    document.getElementById('wsdList').innerHTML = '';
    document.getElementById('skyList').innerHTML = '';

    fetch(`/api/weather?nx=${nx}&ny=${ny}`)
        .then(response => response.json())
        .then(data => {
            const tmpList = document.getElementById('tmpList');
            const popList = document.getElementById('popList');
            const rehList = document.getElementById('rehList');
            const wsdList = document.getElementById('wsdList');
            const skyList = document.getElementById('skyList');
            const ptyList = document.getElementById('ptyList');

            const tmpItem = document.createElement('li');
            tmpItem.textContent = '🌡️기온: ' + data.TMP +'℃';
            tmpList.appendChild(tmpItem);

            const popItem = document.createElement('li');
            popItem.textContent = '☔강수확률: '+data.POP +'%';
            popList.appendChild(popItem);

            const ptyItem = document.createElement('li');
            ptyItem.textContent = '☔강수형태: ' + data.PTY;
            ptyList.appendChild(ptyItem);

            const rehItem = document.createElement('li');
            rehItem.textContent = '💧습도: '+ data.REH+'%';
            rehList.appendChild(rehItem);

            const wsdItem = document.createElement('li');
            wsdItem.textContent = '🌬️풍속: '+data.WSD+'m/s';
            wsdList.appendChild(wsdItem);

            const skyItem = document.createElement('li');
            skyItem.textContent = '🏳️‍⚧️하늘상태: ' + data.SKY;
            skyList.appendChild(skyItem);
        })
        .catch(error => console.error(error));
}

/**
 * 주간 날씨 정보를 API에서 가져와 화면에 표시합니다.
 */
function getWeek(){
    var city = document.getElementById('city').value;

    // 기존 목록 비우기
    document.getElementById('taMinList').innerHTML = '';

    fetch(`/api/week?city=${city}`)
        .then(response=>response.json())
        .then(data=> {
            const weekInfo = document.getElementById('taMinList');
            const options = {month: '2-digit', day: '2-digit'};
            const dataToSend = [];
            const today = new Date();
            for (let i = 3; i <= 10; i++) {
                const targetDate = new Date();
                targetDate.setDate(today.getDate() + i);
                const formattedDate = targetDate.toLocaleDateString(undefined, options);

                const temp = `${data['taMin' + i]}℃ / ${data['taMax' + i]}℃`;
                dataToSend.push(`🌡️${formattedDate} ${temp}`);
            }

            dataToSend.forEach((item) => {
                const li = document.createElement('li');
                li.textContent = item;
                weekInfo.appendChild(li);
            });
        })
}

// === 알림 및 SSE 관련 기능 ===

/**
 * SSE를 통해 서버로부터 알림을 받기 위한 연결을 설정합니다.
 * @param {Element} button - 이벤트를 발생시킨 버튼 요소
 */
function alarm(button) {
    var id = button.getAttribute('data-id');
    var eventSource = new EventSource('/sub/' + id, { withCredentials: true });
    eventSource.addEventListener('sse', function(event) {
        const data = event.data;
        const lastEventId = event.lastEventId;
        console.log('서버 응답:', data);
        console.log('확인',lastEventId);
    });
    eventSource.addEventListener('error', function(error) {
        console.error('오류 발생:', error);
    });
}

/**
 * 알림 목록을 가져와 모달에 표시합니다.
 * @param {Element} button - 이벤트를 발생시킨 버튼 요소
 */
function alarmlist(button) {
    var Id = button.getAttribute('data-id');
    var modal = document.getElementById("NotificationModal");
    var modalContent = modal.querySelector(".modal-body");
    modalContent.style.maxHeight = "400px";
    modalContent.style.overflowY = "auto";
    modalContent.innerHTML = "";

    $.ajax({
        url: '/notifications/' + Id,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            var notifications = data.notificationResponses;
            for (var i = 0; i < notifications.length; i++) {
                var notification = notifications[i];
                var notificationDiv = document.createElement("div");
                notificationDiv.className = "notification-item";
                var h5Element = document.createElement("h5");
                h5Element.textContent = "📬";
                var nameParagraph = document.createElement("p");
                nameParagraph.textContent = "작성자: " + notification.writer;
                var messageParagraph = document.createElement("p");
                messageParagraph.textContent = "메시지: " + notification.message;
                var boardId = notification.board_no;
                notificationDiv.setAttribute('data-board-id', boardId);
                var deleteButton = document.createElement("button");
                deleteButton.textContent = "삭제";
                deleteButton.className = "btn btn-sm btn-danger";
                deleteButton.setAttribute('data-notification-id', notification.id);
                deleteButton.onclick = function(event) {
                    event.stopPropagation();
                    var notificationId = this.getAttribute('data-notification-id');
                    deleteNotification(notificationId, function() {
                        $('#NotificationModal').modal('hide');
                    });
                };
                notificationDiv.addEventListener('click', function () {
                    var boardId = this.getAttribute('data-board-id');
                    handleNotificationClick(boardId, notification.id);
                });
                notificationDiv.appendChild(h5Element);
                notificationDiv.appendChild(nameParagraph);
                notificationDiv.appendChild(messageParagraph);
                notificationDiv.appendChild(deleteButton);
                modalContent.appendChild(notificationDiv);
            }
            $('#NotificationModal').modal('show');
        },
        error: function (error) {
            console.error("알림 데이터를 가져오는 데 실패했습니다:", error);
        },
    });
}

/**
 * 알림을 삭제하는 AJAX 요청을 보냅니다.
 * @param {string} notificationId - 삭제할 알림 ID
 * @param {Function} callback - 삭제 성공 시 호출될 콜백 함수
 */
function deleteNotification(notificationId, callback) {
    $.ajax({
        url: '/deletenotification/' + notificationId,
        type: 'POST',
        dataType: 'text',
        success: function (response) {
            console.log('알림 삭제 성공:', response);
            if (typeof callback === 'function') {
                callback();
            }
        },
        error: function (error) {
            console.error('알림 삭제 오류:', error);
        }
    });
}

/**
 * 알림 클릭 시 해당 게시물로 이동하고 알림을 삭제합니다.
 * @param {string} boardId - 게시물 ID
 * @param {string} notificationId - 알림 ID
 */
function handleNotificationClick(boardId, notificationId) {
    deleteNotification(notificationId, function () {
        var postUrl = '/read/' + boardId;
        window.location.href = postUrl;
    });
}
