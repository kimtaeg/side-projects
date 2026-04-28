using UnityEngine;
using System.Collections;
using System.Collections.Generic;


public class Player : MonoBehaviour {

    //프라이빗이나 퍼블릭 상관없이 직접 값을 넣어주게 해준다.
    [SerializeField]
        // 속도 조절
    private float moveSpeed;
    [SerializeField]
    private GameObject[] weapons;
    private int weaponIndex = 0;
    [SerializeField]
    private Transform shootTransform;

    // 미사일 시간
    [SerializeField]
    private float shootInterval = 0.05f;
    //최근에 쏜 미사일 시간을 저장해주는 것
    private float lastShootTime = 0f;
    void Update()
    {
        // //키보드 방향키  왼쪽 오른쪽 누르게 되면 horizontalInput값이 -1또는 1로 받게됨
        // float horizontalInput = Input.GetAxisRaw("Horizontal");
        // // 상하 움직이는거 
        // float verticalInput = Input.GetAxisRaw("Vertical");

        // //구조체
        // Vector3 moveTo = new Vector3(horizontalInput,verticalInput,0f);
        // // 성능 다른 pc에서 똑같은 크기만큼 이동하기 위해서 Time.deltaTime사용
        // transform.position += moveTo * moveSpeed * Time.deltaTime;

        // x값만 변화주는 방법 그치만 이건 양수만 즉 오른쪽키만 갈수잇기때문에 if을 줘야함
        // Vector3 moveTo = new Vector3(moveSpeed * Time.deltaTime,0 ,0);
        // if (Input.GetKey(KeyCode.LeftArrow))
        // {
        //     transform.position -= moveTo;
        // }   else if (Input.GetKey(KeyCode.RightArrow))
        // {
        //     transform.position += moveTo;
        // }


        // 마우스로 움직이는 방법
        Vector3 mousePos = Camera.main.ScreenToWorldPoint(Input.mousePosition);
        // 최대 최소값을 사용해서 캐릭터가 벗어나는걸 막는 법
        float toX = Mathf.Clamp(mousePos.x, -2.35f, 2.35f);
        // 플러스하면 점진적으로 증가하는데 =은 그냥 그 포지션으로 딱 그 만큼오게 해준다.
        transform.position = new Vector3(toX, transform.position.y, transform.position.z);

        // 무기 쏘는 메소드
        if (GameManager.instance.isGameOver == false)
        {
               Shoot();
        }
    }

    void Shoot()
    {
        //미사일 시간
        // 10 - 0 > 0.05
        if(Time.time - lastShootTime > shootInterval)
        {
            //Quaternion.identity이거는 회전시키는 것
            Instantiate(weapons[weaponIndex], shootTransform.position, Quaternion.identity);
            lastShootTime = Time.time;
        }
    }

    private void OnTriggerEnter2D(Collider2D other) {
        if(other.gameObject.tag == "Enemy"|| other.gameObject.tag == "Boss")
        {
            GameManager.instance.SetGameOver();
            Destroy(gameObject);

        } else if(other.gameObject.tag == "Coin")
        {
            GameManager.instance.IncreaseCoin();
            Destroy(other.gameObject);

        } 
    }

    public void Upgrade()
    {
        weaponIndex += 1;
        if(weaponIndex >= weapons.Length)
        {
            weaponIndex = weapons.Length - 1;
        }
    }
}
